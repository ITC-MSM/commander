package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.ZoneChangeEvent
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class OssificationScenarioTest : ScenarioTestBase() {
    init {
        cardRegistry.register(card("Test Walker") {
            manaCost = "{2}{U}"
            typeLine = "Planeswalker — Jace"
            startingLoyalty = 4
        })
        cardRegistry.register(card("Test Nonbasic Plains") {
            typeLine = "Land — Plains"
        })
        cardRegistry.register(card("Test Removal") {
            manaCost = "{0}"
            typeLine = "Instant"
            spell { effect = Effects.Destroy(target("permanent", TargetPermanent())) }
        })
        cardRegistry.register(card("Test Sequential Sweep") {
            manaCost = "{0}"
            typeLine = "Instant"
            spell {
                effect = Effects.DestroyAll(GameObjectFilter.Enchantment)
                    .then(Effects.DestroyAll(GameObjectFilter.Creature))
            }
        })

        for (victim in listOf("Grizzly Bears", "Test Walker")) {
            test("exiles $victim and returns it immediately with no return trigger") {
                val game = board(victim).build()
                val victimId = game.findPermanent(victim)!!
                exileVictim(game, victim)
                game.isInExile(2, victim) shouldBe true
                val aura = game.findPermanent("Ossification")!!
                for (viewer in listOf(1, 2)) {
                    game.getClientState(viewer).cards.getValue(aura).linkedExile shouldBe listOf(victimId)
                }
                game.castSpell(1, "Test Removal", aura).error shouldBe null
                game.passPriority().error shouldBe null
                val result = game.passPriority()
                result.error shouldBe null
                game.isOnBattlefield(victim) shouldBe true
                game.state.stack.size shouldBe 0
                game.state.zoneReturns.size shouldBe 0
                result.events.filterIsInstance<ZoneChangeEvent>().map { it.entityName } shouldContain victim
                if (victim == "Test Walker") {
                    game.state.getEntity(game.findPermanent(victim)!!)!!.get<CountersComponent>()!!
                        .getCount(CounterType.LOYALTY) shouldBe 4
                }
            }
        }

        test("a returned creature participates in the next instruction of the same spell") {
            val game = board().withCardInHand(1, "Test Sequential Sweep").build()
            exileVictim(game)
            game.castSpell(1, "Test Sequential Sweep").error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Grizzly Bears") shouldBe true
            game.isInExile(2, "Grizzly Bears") shouldBe false
        }

        test("destroying the enchanted land makes the Aura leave and returns the creature without a trigger") {
            val game = board().build()
            val land = game.findPermanent("Plains")!!
            exileVictim(game)
            game.castSpell(1, "Test Removal", land).error shouldBe null
            game.passPriority()
            game.passPriority().error shouldBe null
            game.isInGraveyard(1, "Ossification") shouldBe true
            game.isOnBattlefield("Grizzly Bears") shouldBe true
            game.state.stack.size shouldBe 0
        }

        test("removing Ossification before its entry trigger resolves does not exile the target") {
            val game = board().build()
            castAura(game)
            game.selectTargets(listOf(game.findPermanent("Grizzly Bears")!!)).error shouldBe null
            game.castSpell(1, "Test Removal", game.findPermanent("Ossification")!!).error shouldBe null
            game.resolveStack()
            game.isOnBattlefield("Grizzly Bears") shouldBe true
            game.state.zoneReturns.size shouldBe 0
        }

        for ((owner, host) in listOf(1 to "Test Nonbasic Plains", 2 to "Forest", 1 to "Hill Giant")) {
            test("cannot enchant player $owner's $host") {
                val game = board().withCardOnBattlefield(owner, host).build()
                game.castSpell(1, "Ossification", game.findPermanent(host)!!).error.shouldNotBeNull()
            }
        }

        test("entry trigger offers only opposing creatures and planeswalkers") {
            val game = board().withCardOnBattlefield(1, "Hill Giant")
                .withCardOnBattlefield(2, "Test Walker").withCardOnBattlefield(2, "Forest").build()
            castAura(game)
            val decision = game.state.pendingDecision.shouldBeInstanceOf<ChooseTargetsDecision>()
            val legal = decision.legalTargets.values.flatten()
            legal shouldContain game.findPermanent("Grizzly Bears")!!
            legal shouldContain game.findPermanent("Test Walker")!!
            legal shouldNotContain game.findPermanent("Hill Giant")!!
            legal shouldNotContain game.findPermanent("Forest")!!
        }

        test("losing control of the enchanted basic land removes the Aura and returns the creature") {
            val steal = card("Test Steal Land") {
                manaCost = "{0}"
                typeLine = "Instant"
                spell { effect = Effects.GainControl(target("land", TargetPermanent())) }
            }
            cardRegistry.register(steal)
            val game = board().withCardInHand(2, "Test Steal Land").build()
            val land = game.findPermanent("Plains")!!
            exileVictim(game)
            game.passPriority()
            game.castSpell(2, "Test Steal Land", land).error shouldBe null
            game.resolveStack()
            game.state.projectedState.getController(land) shouldBe game.player2Id
            game.isInGraveyard(1, "Ossification") shouldBe true
            game.isOnBattlefield("Grizzly Bears") shouldBe true
            game.state.stack.size shouldBe 0
        }

        test("an exiled token never returns") {
            val game = board().withCardOnBattlefield(2, "Hill Giant", isToken = true).build()
            exileVictim(game, "Hill Giant")
            game.castSpell(1, "Test Removal", game.findPermanent("Ossification")!!).error shouldBe null
            game.resolveStack()
            game.isOnBattlefield("Hill Giant") shouldBe false
            game.state.zoneReturns.size shouldBe 0
        }
    }

    private fun board(victim: String = "Grizzly Bears") = scenario().withPlayers()
        .withLandsOnBattlefield(1, "Plains", 2)
        .withCardOnBattlefield(2, victim)
        .withCardInHand(1, "Ossification")
        .withCardInHand(1, "Test Removal")
        .withCardInLibrary(1, "Island")
        .withCardInLibrary(2, "Island")

    private fun castAura(game: TestGame) {
        game.castSpell(1, "Ossification", game.findPermanent("Plains")!!).error shouldBe null
        game.resolveStack()
        game.state.pendingDecision.shouldNotBeNull()
    }

    private fun exileVictim(game: TestGame, victim: String = "Grizzly Bears") {
        castAura(game)
        game.selectTargets(listOf(game.findPermanent(victim)!!)).error shouldBe null
        game.resolveStack()
    }
}
