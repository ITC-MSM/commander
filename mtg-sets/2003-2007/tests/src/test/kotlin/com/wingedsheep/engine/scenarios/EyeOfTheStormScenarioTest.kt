package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.EyeOfTheStorm
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

/**
 * Eye of the Storm — {5}{U}{U} Enchantment (Ravnica: City of Guilds #48)
 *
 * "Whenever a player casts an instant or sorcery card, exile it. Then that player copies each
 *  instant or sorcery card exiled with this enchantment. For each copy, the player may cast the
 *  copy without paying its mana cost."
 */
class EyeOfTheStormScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + EyeOfTheStorm)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.putPermanentOnBattlefield(d.player1, "Eye of the Storm")
        return d
    }

    /**
     * Drive the stack to empty. [caster] answers every decision: it casts up to [castCopies]
     * copies (declining the rest) and aims every Bolt at [boltTarget].
     */
    fun GameTestDriver.drain(caster: EntityId, boltTarget: EntityId, castCopies: Int): List<Int> {
        val offered = mutableListOf<Int>()
        var cast = 0
        var guard = 0
        while ((state.stack.isNotEmpty() || state.pendingDecision != null) && guard < 80) {
            val decision = state.pendingDecision
            when {
                decision is ChooseTargetsDecision -> {
                    decision.playerId shouldBe caster
                    submitTargetSelection(caster, listOf(boltTarget)).error shouldBe null
                }
                decision is SelectCardsDecision -> {
                    withClue("the caster, not Eye's controller, chooses which copies to cast") {
                        decision.playerId shouldBe caster
                    }
                    offered += decision.options.size
                    if (cast < castCopies) {
                        cast++
                        submitCardSelection(caster, listOf(decision.options.first())).error shouldBe null
                    } else {
                        submitCardSelection(caster, emptyList()).error shouldBe null
                    }
                }
                decision != null -> autoResolveDecision()
                else -> bothPass()
            }
            guard++
        }
        return offered
    }

    fun GameTestDriver.castBolt(caster: EntityId, at: EntityId) {
        val bolt = putCardInHand(caster, "Lightning Bolt")
        giveMana(caster, Color.RED, 1)
        castSpell(caster, bolt, listOf(at)).error shouldBe null
    }

    test("exiles the cast card and the caster casts a free copy of it") {
        val d = driver()
        d.castBolt(d.player1, d.player2)
        val offered = d.drain(d.player1, d.player2, castCopies = 1)

        withClue("only one card was in the pile") { offered.first() shouldBe 1 }
        withClue("the original never resolved; the copy dealt 3 exactly once (a copy doesn't re-trigger)") {
            d.getLifeTotal(d.player2) shouldBe 17
        }
        withClue("the Bolt card sits in exile, not the graveyard") {
            d.getExileCardNames(d.player1) shouldContainExactlyInAnyOrder listOf("Lightning Bolt")
            d.getGraveyardCardNames(d.player1).contains("Lightning Bolt") shouldBe false
        }
    }

    test("another player's cast copies every card in the pile, including ones they don't own") {
        val d = driver()
        d.castBolt(d.player1, d.player2)
        d.drain(d.player1, d.player2, castCopies = 1)
        d.getLifeTotal(d.player2) shouldBe 17

        // Hand priority to the opponent with an empty stack.
        d.passPriority(d.player1)
        d.castBolt(d.player2, d.player1)
        val offered = d.drain(d.player2, d.player1, castCopies = 2)

        withClue("both exiled Bolts were copied for the opponent") { offered.first() shouldBe 2 }
        withClue("the opponent cast both copies at Eye's controller") {
            d.getLifeTotal(d.player1) shouldBe 14
        }
        d.getExileCardNames(d.player2) shouldContainExactlyInAnyOrder listOf("Lightning Bolt")
    }

    test("declining casts nothing, and the uncast copy ceases to exist") {
        val d = driver()
        d.castBolt(d.player1, d.player2)
        d.drain(d.player1, d.player2, castCopies = 0)

        d.getLifeTotal(d.player2) shouldBe 20
        withClue("only the original card remains in exile — the copy is gone (CR 707.10a)") {
            d.getExileCardNames(d.player1) shouldContainExactlyInAnyOrder listOf("Lightning Bolt")
        }
    }
})
