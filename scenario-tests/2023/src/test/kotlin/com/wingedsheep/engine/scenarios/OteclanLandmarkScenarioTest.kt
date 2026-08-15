package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.DoubleFacedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.OteclanLandmark
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain as stringShouldContain

/**
 * Scenario tests for Oteclan Landmark // Oteclan Levitator (LCI #29).
 *
 * Front face — Oteclan Landmark ({W}, Artifact):
 *   "When this artifact enters, scry 2."
 *   "Craft with artifact {2}{W}" — exactly one artifact material (CR 702.167).
 *
 * Back face — Oteclan Levitator (Artifact Creature — Golem, 1/4):
 *   Flying. "Whenever this creature attacks, target attacking creature without flying
 *   gains flying until end of turn."
 */
class OteclanLandmarkScenarioTest : FunSpec({

    // Test-local cards: a vanilla artifact as craft material, and a ground creature
    // both as an illegal (non-artifact) craft material and as the attack-trigger target.
    val testRelic = CardDefinition.artifact(
        name = "Test Relic",
        manaCost = ManaCost.parse("{1}"),
        oracleText = ""
    )
    val groundTrooper = CardDefinition.creature(
        name = "Test Ground Trooper",
        manaCost = ManaCost.parse("{1}{W}"),
        subtypes = setOf(Subtype("Soldier")),
        power = 2,
        toughness = 2,
        oracleText = ""
    )

    val projector = StateProjector()

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(OteclanLandmark, testRelic, groundTrooper))
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        return driver
    }

    // The craft is the front face's only activated ability.
    fun craftAbilityId() = OteclanLandmark.activatedAbilities.single().id

    fun cardName(driver: GameTestDriver, id: EntityId): String? =
        driver.state.getEntity(id)?.get<CardComponent>()?.name

    // Craft Oteclan Landmark with a Test Relic material; returns (landmarkId, relicId).
    // Leaves the game in the crafting player's precombat main with an empty stack.
    fun craftLandmark(driver: GameTestDriver, player: EntityId): Pair<EntityId, EntityId> {
        val landmark = driver.putPermanentOnBattlefield(player, "Oteclan Landmark")
        val relic = driver.putPermanentOnBattlefield(player, "Test Relic")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(player, Color.WHITE, 3)
        driver.submitSuccess(
            ActivateAbility(
                playerId = player,
                sourceId = landmark,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(relic))
            )
        )
        driver.bothPass() // resolve the craft ability
        return landmark to relic
    }

    test("front face: casting Oteclan Landmark fires the ETB trigger and scries 2") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Known cards on top of the library (prepended: Forest on top, Mountain second).
        driver.putCardOnTopOfLibrary(p1, "Mountain")
        driver.putCardOnTopOfLibrary(p1, "Forest")

        val landmark = driver.putCardInHand(p1, "Oteclan Landmark")
        driver.giveMana(p1, Color.WHITE, 1)
        driver.castSpell(p1, landmark).isSuccess shouldBe true

        driver.bothPass() // resolve the spell — the Landmark enters, ETB trigger queued
        driver.bothPass() // resolve the ETB trigger — pauses for the scry decision

        // Scry 2 presents a card selection over the top two library cards.
        val decision = driver.pendingDecision as SelectCardsDecision
        decision.playerId shouldBe p1
        decision.options.size shouldBe 2

        // Put both looked-at cards on the bottom.
        driver.submitCardSelection(p1, decision.options)
        while (driver.pendingDecision != null) driver.autoResolveDecision()

        // A Plains surfaces on top; Forest and Mountain are now the bottom two cards.
        val library = driver.state.getLibrary(p1)
        cardName(driver, library.first()) shouldBe "Plains"
        library.takeLast(2).map { cardName(driver, it) }.toSet() shouldBe setOf("Forest", "Mountain")

        // The Landmark itself resolved onto the battlefield as its front face.
        val entity = driver.state.getEntity(landmark)
        entity.shouldNotBeNull()
        entity.get<CardComponent>()!!.name shouldBe "Oteclan Landmark"
        entity.get<DoubleFacedComponent>()!!.currentFace shouldBe DoubleFacedComponent.Face.FRONT
    }

    test("craft with artifact exiles the material and returns transformed as Oteclan Levitator (1/4 flier)") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val (landmark, relic) = craftLandmark(driver, p1)

        // Material is in exile.
        driver.getExile(p1) shouldContain relic

        // Source returned to the battlefield as the back face.
        val entity = driver.state.getEntity(landmark)
        entity.shouldNotBeNull()
        val card = entity.get<CardComponent>()
        card.shouldNotBeNull()
        card.name shouldBe "Oteclan Levitator"
        card.typeLine.cardTypes shouldBe setOf(CardType.ARTIFACT, CardType.CREATURE)
        card.typeLine.subtypes shouldContain Subtype("Golem")
        entity.get<DoubleFacedComponent>()!!.currentFace shouldBe DoubleFacedComponent.Face.BACK

        // Back-face characteristics: 1/4 with flying.
        val projected = projector.project(driver.state)
        projected.getPower(landmark) shouldBe 1
        projected.getToughness(landmark) shouldBe 4
        projected.hasKeyword(landmark, Keyword.FLYING) shouldBe true
    }

    test("back face: attack trigger grants flying to a target attacking creature without flying") {
        val driver = setup()
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)

        // Two ground attackers so the trigger has more than one legal target (a single legal
        // choice could be auto-selected without surfacing the decision).
        val trooper = driver.putCreatureOnBattlefield(p1, "Test Ground Trooper")
        val trooper2 = driver.putCreatureOnBattlefield(p1, "Test Ground Trooper")
        val (landmark, _) = craftLandmark(driver, p1)

        // All entered this turn — clear summoning sickness so they can attack.
        driver.removeSummoningSickness(landmark)
        driver.removeSummoningSickness(trooper)
        driver.removeSummoningSickness(trooper2)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        val attackResult = driver.declareAttackers(p1, listOf(landmark, trooper, trooper2), p2)
        attackResult.error shouldBe null

        // The Levitator's attack trigger asks for a target: only the non-flying attackers are
        // legal — the Levitator itself has flying and can't be chosen.
        val decision = driver.pendingDecision as ChooseTargetsDecision
        decision.legalTargets[0]!! shouldContain trooper
        decision.legalTargets[0]!! shouldContain trooper2
        decision.legalTargets[0]!! shouldNotContain landmark
        driver.submitTargetSelection(p1, listOf(trooper))

        driver.bothPass() // resolve the attack trigger

        val projected = projector.project(driver.state)
        projected.hasKeyword(trooper, Keyword.FLYING) shouldBe true
        projected.hasKeyword(trooper2, Keyword.FLYING) shouldBe false
    }

    test("craft rejects two materials — exactly one artifact (maxCount = 1)") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val landmark = driver.putPermanentOnBattlefield(p1, "Oteclan Landmark")
        val relic1 = driver.putPermanentOnBattlefield(p1, "Test Relic")
        val relic2 = driver.putPermanentOnBattlefield(p1, "Test Relic")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(p1, Color.WHITE, 3)

        val result = driver.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = landmark,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(relic1, relic2))
            )
        )
        result.isSuccess shouldBe false
        result.error.shouldNotBeNull() stringShouldContain "at most"

        // Nothing was exiled; the Landmark is still its front face on the battlefield.
        driver.getExile(p1).size shouldBe 0
        driver.state.getEntity(landmark)!!.get<CardComponent>()!!.name shouldBe "Oteclan Landmark"
    }

    test("craft rejects a non-artifact creature as material") {
        val driver = setup()
        val p1 = driver.activePlayer!!

        val landmark = driver.putPermanentOnBattlefield(p1, "Oteclan Landmark")
        val trooper = driver.putCreatureOnBattlefield(p1, "Test Ground Trooper")
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(p1, Color.WHITE, 3)

        val result = driver.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = landmark,
                abilityId = craftAbilityId(),
                costPayment = AdditionalCostPayment(exiledCards = listOf(trooper))
            )
        )
        result.isSuccess shouldBe false

        // The creature stays on the battlefield; nothing transformed.
        driver.findPermanent(p1, "Test Ground Trooper").shouldNotBeNull()
        driver.state.getEntity(landmark)!!.get<CardComponent>()!!.name shouldBe "Oteclan Landmark"
    }
})
