package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.DoubleFacedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.TithingBlade
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Tithing Blade // Consuming Sepulcher (LCI #128).
 *
 * Front face — Tithing Blade ({1}{B}, Artifact):
 *   "When this artifact enters, each opponent sacrifices a creature of their choice.
 *    Craft with creature {4}{B}"
 * Back face — Consuming Sepulcher (Artifact):
 *   "At the beginning of your upkeep, each opponent loses 1 life and you gain 1 life."
 *
 * Covers:
 *  - ETB edict: the sacrifice is the OPPONENT's choice — the engine pauses with a
 *    [SelectCardsDecision] for the opponent, whose options are only their own creatures.
 *  - Craft (CR 702.167): exactly one creature material, from the battlefield or from the
 *    graveyard, is exiled and the card returns transformed as Consuming Sepulcher — an
 *    Artifact that is NOT a creature.
 *  - Back-face drain: at the beginning of the controller's upkeep (and only theirs), each
 *    opponent loses 1 life and the controller gains 1.
 *  - Negative: a non-creature artifact is rejected as craft material; two materials exceed
 *    the exact-one count.
 */
class TithingBladeScenarioTest : FunSpec({

    // Local non-creature artifact — illegal craft material for "Craft with creature".
    val trinket = CardDefinition.artifact(
        name = "Test Trinket",
        manaCost = ManaCost.parse("{1}")
    )

    val projector = StateProjector()

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(trinket))
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 40),
            skipMulligans = true
        )
        return driver
    }

    // The craft is the front face's only activated ability.
    fun craftAbilityId() = TithingBlade.activatedAbilities.single().id

    /** Pass priority until a pending decision appears (a resolving effect pauses there). */
    fun GameTestDriver.passUntilDecision(maxPasses: Int = 10) {
        repeat(maxPasses) { if (pendingDecision == null) bothPass() }
    }

    test("ETB: each opponent sacrifices a creature of their own choice") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)

        // Opponent has two creatures -> a real choice; controller's own creature must be spared.
        val oppBears = driver.putCreatureOnBattlefield(p2, "Grizzly Bears")
        val oppGiant = driver.putCreatureOnBattlefield(p2, "Hill Giant")
        val myBears = driver.putCreatureOnBattlefield(p1, "Grizzly Bears")

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val blade = driver.putCardInHand(p1, "Tithing Blade")
        driver.giveMana(p1, Color.BLACK, 2)
        driver.castSpell(p1, blade).isSuccess shouldBe true

        // Resolve the spell, then its ETB trigger — which pauses on the opponent's choice.
        driver.passUntilDecision()

        val decision = driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.playerId shouldBe p2
        decision.options.shouldContainExactlyInAnyOrder(listOf(oppBears, oppGiant))

        // The opponent picks the Hill Giant.
        driver.submitCardSelection(p2, listOf(oppGiant))

        driver.getGraveyard(p2).shouldContain(oppGiant)
        driver.findPermanent(p2, "Hill Giant") shouldBe null
        driver.findPermanent(p2, "Grizzly Bears") shouldBe oppBears
        driver.findPermanent(p1, "Grizzly Bears") shouldBe myBears
        driver.findPermanent(p1, "Tithing Blade") shouldBe blade
    }

    test("craft with a battlefield creature: material exiled, returns as Consuming Sepulcher (not a creature)") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val blade = driver.putPermanentOnBattlefield(p1, "Tithing Blade")
        val bears = driver.putCreatureOnBattlefield(p1, "Grizzly Bears")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(p1, Color.BLACK, 5)

        driver.submitSuccess(
            ActivateAbility(
                playerId = p1,
                sourceId = blade,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(bears))
            )
        )
        driver.bothPass()

        // Material exiled.
        driver.getExile(p1).shouldContain(bears)

        // Source returned to the battlefield as its back face.
        val container = driver.state.getEntity(blade)
        container.shouldNotBeNull()
        val card = container.get<CardComponent>()
        card.shouldNotBeNull()
        card.name shouldBe "Consuming Sepulcher"
        card.typeLine.cardTypes shouldBe setOf(CardType.ARTIFACT)

        val dfc = container.get<DoubleFacedComponent>()
        dfc.shouldNotBeNull()
        dfc.currentFace shouldBe DoubleFacedComponent.Face.BACK

        // Consuming Sepulcher is an Artifact — NOT a creature — in the projection too.
        projector.project(driver.state).isCreature(blade) shouldBe false
    }

    test("craft with a creature card from the graveyard works as material (CR 702.167b)") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val blade = driver.putPermanentOnBattlefield(p1, "Tithing Blade")
        val deadBears = driver.putCardInGraveyard(p1, "Grizzly Bears")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(p1, Color.BLACK, 5)

        driver.submitSuccess(
            ActivateAbility(
                playerId = p1,
                sourceId = blade,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(deadBears))
            )
        )
        driver.bothPass()

        driver.getExile(p1).shouldContain(deadBears)
        driver.getGraveyard(p1).contains(deadBears) shouldBe false
        driver.state.getEntity(blade)!!.get<CardComponent>()!!.name shouldBe "Consuming Sepulcher"
    }

    test("back face: at the beginning of your upkeep each opponent loses 1 life and you gain 1 — and only on YOUR upkeep") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)

        val blade = driver.putPermanentOnBattlefield(p1, "Tithing Blade")
        val bears = driver.putCreatureOnBattlefield(p1, "Grizzly Bears")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(p1, Color.BLACK, 5)
        driver.submitSuccess(
            ActivateAbility(
                playerId = p1,
                sourceId = blade,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(bears))
            )
        )
        driver.bothPass()
        driver.state.getEntity(blade)!!.get<CardComponent>()!!.name shouldBe "Consuming Sepulcher"

        driver.getLifeTotal(p1) shouldBe 20
        driver.getLifeTotal(p2) shouldBe 20

        // Opponent's upkeep — "your upkeep" means the controller's, so no trigger fires.
        driver.passPriorityUntil(Step.UPKEEP)
        driver.activePlayer shouldBe p2
        driver.stackSize shouldBe 0
        driver.getLifeTotal(p1) shouldBe 20
        driver.getLifeTotal(p2) shouldBe 20

        // Cross the opponent's turn to our next upkeep — the drain trigger is on the stack.
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.activePlayer shouldBe p1
        driver.stackSize shouldBe 1
        driver.bothPass()

        driver.getLifeTotal(p1) shouldBe 21
        driver.getLifeTotal(p2) shouldBe 19
    }

    test("craft rejects a non-creature artifact material and rejects more than one material") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val blade = driver.putPermanentOnBattlefield(p1, "Tithing Blade")
        val badMaterial = driver.putPermanentOnBattlefield(p1, "Test Trinket")
        val bearsA = driver.putCreatureOnBattlefield(p1, "Grizzly Bears")
        val bearsB = driver.putCreatureOnBattlefield(p1, "Grizzly Bears")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(p1, Color.BLACK, 5)

        // Non-creature artifact is not a legal "Craft with creature" material.
        driver.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = blade,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(badMaterial))
            )
        ).isSuccess shouldBe false

        // "Craft with creature" is exactly one material — two creatures must be rejected.
        driver.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = blade,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(bearsA, bearsB))
            )
        ).isSuccess shouldBe false

        // Nothing moved: the blade is still the front face on the battlefield, nothing exiled.
        driver.findPermanent(p1, "Tithing Blade") shouldBe blade
        driver.findPermanent(p1, "Test Trinket") shouldBe badMaterial
        driver.getExile(p1).isEmpty() shouldBe true
    }
})
