package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.DoubleFacedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.ClayFiredBricks
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Clay-Fired Bricks // Cosmium Kiln (LCI #6, CR 702.167).
 *
 * Front face — Clay-Fired Bricks ({1}{W} Artifact):
 *   "When this artifact enters, search your library for a basic Plains card, reveal it,
 *    put it into your hand, then shuffle. You gain 2 life.
 *    Craft with artifact {5}{W}{W}"
 *
 * Back face — Cosmium Kiln (Artifact):
 *   "When this artifact enters, create two 1/1 colorless Gnome artifact creature tokens.
 *    Creatures you control get +1/+1."
 *
 * Covers:
 *  - Front ETB: basic-Plains-only library search (non-Plains basics excluded) to hand,
 *    then the sequenced 2-life gain.
 *  - Craft end-to-end: mana + exactly-one-artifact material paid, source returns
 *    transformed as Cosmium Kiln, and the back face's ETB (a fresh battlefield entry,
 *    not a transform — CR 701.27a) creates the two Gnome tokens.
 *  - Back-face anthem: creatures you control (including the Gnome tokens) get +1/+1 in
 *    projection; opponent's creatures are unaffected.
 *  - Validation: a non-artifact creature is rejected as craft material.
 */
class ClayFiredBricksScenarioTest : FunSpec({

    // Plain artifact to use as legal craft material.
    val testRelic = CardDefinition.artifact(
        name = "Test Relic",
        manaCost = ManaCost.parse("{1}")
    )

    val projector = StateProjector()

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(testRelic))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true)
        return driver
    }

    // The front face's only activated ability is the Craft.
    fun craftAbilityId() = ClayFiredBricks.activatedAbilities.single().id

    /** Craft Clay-Fired Bricks (already on the battlefield) using [material], resolving the ability. */
    fun GameTestDriver.craftBricks(
        player: com.wingedsheep.sdk.model.EntityId,
        bricks: com.wingedsheep.sdk.model.EntityId,
        material: com.wingedsheep.sdk.model.EntityId
    ) {
        giveMana(player, Color.WHITE, 7)
        submitSuccess(
            ActivateAbility(
                playerId = player,
                sourceId = bricks,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(material))
            )
        )
        bothPass() // resolve the craft ability -> back face enters, its ETB trigger goes on the stack
    }

    test("front ETB searches for a basic Plains (only), puts it into hand, and gains 2 life") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        // Library is all Mountains (basic, but not Plains) plus this one Plains.
        val plains = driver.putCardOnTopOfLibrary(p1, "Plains")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val bricks = driver.putCardInHand(p1, "Clay-Fired Bricks")
        driver.giveMana(p1, Color.WHITE, 2)
        val lifeBefore = driver.getLifeTotal(p1)

        driver.castSpell(p1, bricks).isSuccess shouldBe true
        driver.bothPass() // resolve the artifact spell -> ETB trigger on the stack
        driver.bothPass() // resolve the trigger -> pauses on the library search selection

        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        // Only the basic Plains is searchable — the basic Mountains don't match the filter.
        decision.options shouldBe listOf(plains)

        driver.submitCardSelection(p1, listOf(plains))

        driver.getHand(p1) shouldContain plains
        driver.getLifeTotal(p1) shouldBe lifeBefore + 2
    }

    test("craft with an artifact exiles the material and returns transformed as Cosmium Kiln, whose ETB creates two Gnome tokens") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val bricks = driver.putPermanentOnBattlefield(p1, "Clay-Fired Bricks")
        val relic = driver.putPermanentOnBattlefield(p1, "Test Relic")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.craftBricks(p1, bricks, relic)

        // Material exiled.
        driver.state.getZone(ZoneKey(p1, Zone.EXILE)) shouldContain relic

        // Source returned to the battlefield as the back face.
        val container = driver.state.getEntity(bricks)
        container.shouldNotBeNull()
        val card = container.get<CardComponent>()
        card.shouldNotBeNull()
        card.name shouldBe "Cosmium Kiln"
        card.typeLine.cardTypes shouldBe setOf(CardType.ARTIFACT)

        val dfc = container.get<DoubleFacedComponent>()
        dfc.shouldNotBeNull()
        dfc.currentFace shouldBe DoubleFacedComponent.Face.BACK

        // The craft return is a battlefield entry, so the back face's ETB trigger fired
        // and is on the stack; resolving it creates the two Gnome tokens.
        driver.bothPass()
        val gnomes = driver.getPermanents(p1).filter { driver.getCardName(it) == "Gnome Token" }
        gnomes.size shouldBe 2
    }

    test("Cosmium Kiln's anthem gives your creatures +1/+1 (Gnome tokens are 2/2) but not the opponent's") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)

        val bricks = driver.putPermanentOnBattlefield(p1, "Clay-Fired Bricks")
        val relic = driver.putPermanentOnBattlefield(p1, "Test Relic")
        val myLions = driver.putCreatureOnBattlefield(p1, "Savannah Lions")       // 1/1
        val oppCourser = driver.putCreatureOnBattlefield(p2, "Centaur Courser")   // 3/3
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.craftBricks(p1, bricks, relic)
        driver.bothPass() // resolve the back-face ETB trigger -> two Gnome tokens

        val projected = projector.project(driver.state)

        // My 1/1 Savannah Lions is projected 2/2 under the anthem.
        projected.getPower(myLions) shouldBe 2
        projected.getToughness(myLions) shouldBe 2

        // The 1/1 Gnome tokens are projected 2/2.
        val gnomes = driver.getPermanents(p1).filter { driver.getCardName(it) == "Gnome Token" }
        gnomes.size shouldBe 2
        gnomes.forEach { gnome ->
            projected.getPower(gnome) shouldBe 2
            projected.getToughness(gnome) shouldBe 2
        }

        // Opponent's 3/3 Centaur Courser is unaffected.
        projected.getPower(oppCourser) shouldBe 3
        projected.getToughness(oppCourser) shouldBe 3
    }

    test("rejects a non-artifact creature as craft material") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val bricks = driver.putPermanentOnBattlefield(p1, "Clay-Fired Bricks")
        val lions = driver.putCreatureOnBattlefield(p1, "Savannah Lions") // not an artifact
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(p1, Color.WHITE, 7)

        val result = driver.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = bricks,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(lions))
            )
        )
        result.isSuccess shouldBe false

        // Nothing moved: still the front face on the battlefield, no exile.
        driver.state.getEntity(bricks)!!.get<CardComponent>()!!.name shouldBe "Clay-Fired Bricks"
        driver.state.getZone(ZoneKey(p1, Zone.EXILE)) shouldNotContain lions
    }
})
