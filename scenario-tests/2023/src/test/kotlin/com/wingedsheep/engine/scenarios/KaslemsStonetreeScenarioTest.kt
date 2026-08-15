package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.DoubleFacedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.KaslemsStonetree
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Kaslem's Stonetree // Kaslem's Strider (LCI #197) — Craft transform DFC.
 *
 * Front ({2}{G} Artifact):
 *   "When this artifact enters, look at the top six cards of your library. You may put a land
 *    card from among them onto the battlefield tapped. Put the rest on the bottom in a random
 *    order."
 *   "Craft with Cave {5}{G}" — exactly one Cave you control or Cave card from your graveyard
 *   (CR 702.167a-b).
 *
 * Back (Artifact Creature — Golem, 5/5): vanilla.
 */
class KaslemsStonetreeScenarioTest : FunSpec({

    val projector = StateProjector()

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(
            deck = Deck.of("Mountain" to 40),
            skipMulligans = true
        )
        return driver
    }

    // The craft is the front face's only activated ability.
    fun craftAbilityId() = KaslemsStonetree.activatedAbilities.single().id

    fun GameTestDriver.library(playerId: EntityId): List<EntityId> =
        state.getZone(ZoneKey(playerId, Zone.LIBRARY))

    fun GameTestDriver.battlefieldNames(playerId: EntityId): List<String> =
        state.getZone(ZoneKey(playerId, Zone.BATTLEFIELD)).mapNotNull {
            state.getEntity(it)?.get<CardComponent>()?.name
        }

    test("cast from hand: ETB looks at top six, puts a chosen land onto the battlefield tapped, rest on the bottom") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Seed a known top six: one Forest buried under five non-land cards.
        // putCardOnTopOfLibrary prepends, so the LAST push ends up on top —
        // top six = [bolt5..bolt1, Forest].
        val forest = driver.putCardOnTopOfLibrary(p1, "Forest")
        val bolts = (1..5).map { driver.putCardOnTopOfLibrary(p1, "Lightning Bolt") }

        val stonetree = driver.putCardInHand(p1, "Kaslem's Stonetree")
        driver.giveMana(p1, Color.GREEN, 3)
        driver.castSpell(p1, stonetree)

        driver.bothPass() // resolve the artifact spell -> enters -> ETB trigger on stack
        driver.bothPass() // resolve the trigger -> pauses for the land selection

        driver.isPaused shouldBe true
        driver.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        val select = driver.pendingDecision as SelectCardsDecision
        select.options shouldContain forest

        driver.submitDecision(p1, CardsSelectedResponse(decisionId = select.id, selectedCards = listOf(forest)))

        // "Put the rest on the bottom in a random order" needs no player input.
        driver.isPaused shouldBe false

        // The Forest entered the battlefield tapped; the source is on the battlefield as its front face.
        driver.battlefieldNames(p1) shouldContain "Forest"
        driver.isTapped(forest) shouldBe true
        driver.state.getEntity(stonetree)!!.get<CardComponent>()!!.name shouldBe "Kaslem's Stonetree"

        // The five non-land cards went to the bottom of the library (top = index 0).
        driver.library(p1).takeLast(5).shouldContainExactlyInAnyOrder(bolts)
    }

    test("craft with a Cave you control: Cave exiled, returns transformed as Kaslem's Strider 5/5") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val stonetree = driver.putPermanentOnBattlefield(p1, "Kaslem's Stonetree")
        val cave = driver.putLandOnBattlefield(p1, "Captivating Cave")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(p1, Color.GREEN, 6)

        driver.submitSuccess(
            ActivateAbility(
                playerId = p1,
                sourceId = stonetree,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(cave))
            )
        )
        driver.bothPass() // resolve the craft ability

        // The Cave material is exiled.
        driver.state.getZone(ZoneKey(p1, Zone.EXILE)) shouldContain cave

        // The source is back on the battlefield as its back face.
        val container = driver.state.getEntity(stonetree)
        container.shouldNotBeNull()
        val card = container.get<CardComponent>()
        card.shouldNotBeNull()
        card.name shouldBe "Kaslem's Strider"
        card.typeLine.cardTypes shouldBe setOf(CardType.ARTIFACT, CardType.CREATURE)
        card.typeLine.subtypes shouldContain Subtype("Golem")

        val dfc = container.get<DoubleFacedComponent>()
        dfc.shouldNotBeNull()
        dfc.currentFace shouldBe DoubleFacedComponent.Face.BACK

        // Vanilla 5/5.
        val projected = projector.project(driver.state)
        projected.getPower(stonetree) shouldBe 5
        projected.getToughness(stonetree) shouldBe 5
    }

    test("craft with a Cave card from the graveyard (CR 702.167b)") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val stonetree = driver.putPermanentOnBattlefield(p1, "Kaslem's Stonetree")
        val cave = driver.putCardInGraveyard(p1, "Captivating Cave")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(p1, Color.GREEN, 6)

        driver.submitSuccess(
            ActivateAbility(
                playerId = p1,
                sourceId = stonetree,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(cave))
            )
        )
        driver.bothPass()

        driver.state.getZone(ZoneKey(p1, Zone.EXILE)) shouldContain cave
        driver.state.getEntity(stonetree)!!.get<CardComponent>()!!.name shouldBe "Kaslem's Strider"
        driver.state.getEntity(stonetree)!!.get<DoubleFacedComponent>()!!.currentFace shouldBe
            DoubleFacedComponent.Face.BACK
    }

    test("rejects a non-Cave land as craft material") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val stonetree = driver.putPermanentOnBattlefield(p1, "Kaslem's Stonetree")
        val forest = driver.putLandOnBattlefield(p1, "Forest")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(p1, Color.GREEN, 6)

        val result = driver.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = stonetree,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(forest))
            )
        )
        result.isSuccess shouldBe false

        // Nothing moved: the Stonetree is still on the battlefield as its front face,
        // the Forest is not exiled.
        driver.state.getEntity(stonetree)!!.get<CardComponent>()!!.name shouldBe "Kaslem's Stonetree"
        driver.battlefieldNames(p1) shouldContain "Forest"
    }

    test("rejects more than one Cave (craft with Cave is exactly one material)") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val stonetree = driver.putPermanentOnBattlefield(p1, "Kaslem's Stonetree")
        val cave1 = driver.putLandOnBattlefield(p1, "Captivating Cave")
        val cave2 = driver.putLandOnBattlefield(p1, "Captivating Cave")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(p1, Color.GREEN, 6)

        val result = driver.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = stonetree,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(cave1, cave2))
            )
        )
        result.isSuccess shouldBe false
    }
})
