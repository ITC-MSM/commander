package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Kiora of Salt and Sand (FRA #273) — {1}{G}{U} Legendary Creature — Merfolk Noble 2/4:
 *   Whenever you attack, if you've activated a loyalty ability this turn, untap target attacking
 *   creature. It can't be blocked this turn.
 *   Planeswalkers you control have "[−8]: Create an 8/8 blue Leviathan creature token with
 *   hexproof."
 */
class KioraOfSaltAndSandScenarioTest : ScenarioTestBase() {
    init {
        val ajaniAbilities = cardRegistry.getCard("Ajani Goldmane")!!.script.activatedAbilities
        val ajaniPlusOne = ajaniAbilities.single { (it.cost as? AbilityCost.Loyalty)?.change == 1 }.id
        val leviathanAbility = cardRegistry.getCard("Kiora of Salt and Sand")!!.script.staticAbilities
            .filterIsInstance<GrantActivatedAbility>().single().ability.id

        fun base() = scenario().withPlayers()
            .withCardOnBattlefield(1, "Kiora of Salt and Sand")
            .withCardOnBattlefield(1, "Ajani Goldmane")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withCardOnBattlefield(2, "Hill Giant")
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(2, "Island")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        test("after a loyalty activation, attacking untaps the target and makes it unblockable") {
            val game = base().build()
            val ajani = game.findPermanent("Ajani Goldmane")!!
            val bears = game.findPermanent("Grizzly Bears")!!

            game.execute(ActivateAbility(game.player1Id, ajani, ajaniPlusOne)).error shouldBe null
            game.resolveStack()

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
            withClue("the trigger asks for its target") {
                game.hasPendingDecision() shouldBe true
            }
            game.selectTargets(listOf(bears)).error shouldBe null
            game.resolveStack()

            game.state.getEntity(bears)!!.has<TappedComponent>() shouldBe false
            game.state.projectedState.hasKeyword(bears, AbilityFlag.CANT_BE_BLOCKED) shouldBe true
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
            game.declareBlockers(mapOf("Hill Giant" to listOf("Grizzly Bears"))).error shouldNotBe null
        }

        test("without a loyalty activation this turn, attacking does nothing") {
            val game = base().build()
            val bears = game.findPermanent("Grizzly Bears")!!

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
            game.hasPendingDecision() shouldBe false
            game.resolveStack()

            game.state.getEntity(bears)!!.has<TappedComponent>() shouldBe true
            game.state.projectedState.hasKeyword(bears, AbilityFlag.CANT_BE_BLOCKED) shouldBe false
        }

        test("the activation still counts after that planeswalker has left the battlefield") {
            val game = base()
                .withCardInHand(1, "Lightning Bolt")
                .withLandsOnBattlefield(1, "Mountain", 1)
                .build()
            val ajani = game.findPermanent("Ajani Goldmane")!!
            val bears = game.findPermanent("Grizzly Bears")!!

            game.execute(ActivateAbility(game.player1Id, ajani, ajaniPlusOne)).error shouldBe null
            game.resolveStack()
            game.state = game.state.updateEntity(ajani) {
                it.with(CountersComponent(mapOf(CounterType.LOYALTY to 3)))
            }
            game.castSpell(1, "Lightning Bolt", ajani).error shouldBe null
            game.resolveStack()
            game.isOnBattlefield("Ajani Goldmane") shouldBe false

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
            game.selectTargets(listOf(bears)).error shouldBe null
            game.resolveStack()
            game.state.getEntity(bears)!!.has<TappedComponent>() shouldBe false
        }

        test("planeswalkers you control have −8: create an 8/8 hexproof Leviathan") {
            val game = base().build()
            val ajani = game.findPermanent("Ajani Goldmane")!!
            withClue("Ajani starts at 4 — not enough for −8") {
                game.execute(ActivateAbility(game.player1Id, ajani, leviathanAbility)).error shouldNotBe null
            }
            game.state = game.state.updateEntity(ajani) {
                it.with(CountersComponent(mapOf(CounterType.LOYALTY to 9)))
            }

            game.execute(ActivateAbility(game.player1Id, ajani, leviathanAbility)).error shouldBe null
            game.resolveStack()

            val leviathan = game.findPermanent("Leviathan Token")!!
            game.state.projectedState.getPower(leviathan) shouldBe 8
            game.state.projectedState.getToughness(leviathan) shouldBe 8
            game.state.projectedState.hasKeyword(leviathan, Keyword.HEXPROOF) shouldBe true
            game.state.getEntity(ajani)!!.get<CountersComponent>()!!.getCount(CounterType.LOYALTY) shouldBe 1
        }

        test("an opponent's planeswalker doesn't get the −8") {
            val game = base()
                .withCardOnBattlefield(2, "Chandra Nalaar")
                .build()
            val chandra = game.findPermanent("Chandra Nalaar")!!
            game.state = game.state.updateEntity(chandra) {
                it.with(CountersComponent(mapOf(CounterType.LOYALTY to 9)))
            }
            game.execute(ActivateAbility(game.player2Id, chandra, leviathanAbility)).error shouldNotBe null
        }
    }
}
