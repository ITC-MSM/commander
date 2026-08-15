package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.ValleyQuestcaller
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Valley Questcaller (BLB): "Whenever ONE OR MORE other Rabbits, Bats, Birds, and/or
 * Mice you control enter, scry 1."
 *
 * Regression guards:
 * - Batched (CR 603.3b): two Rabbits entering simultaneously yield ONE scry, not two.
 *   The old per-object ZoneChangeEvent trigger scried once per creature.
 * - "Other": Questcaller (itself a Rabbit) must not scry off its own entry.
 */
class ValleyQuestcallerTest : FunSpec({

    val twoRabbits: CardDefinition = card("Test Rabbit Pair") {
        manaCost = "{W}"
        typeLine = "Sorcery"
        spell {
            effect = Effects.CreateToken(
                count = 2,
                power = 1,
                toughness = 1,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Rabbit")
            )
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(ValleyQuestcaller, twoRabbits))
        return driver
    }

    /** Count how many scry selections occur while resolving everything on the stack. */
    fun GameTestDriver.resolveAllCountingScrys(player: com.wingedsheep.sdk.model.EntityId): Int {
        var scrys = 0
        repeat(10) {
            val decision = pendingDecision
            if (decision is SelectCardsDecision && decision.playerId == player) {
                scrys++
                autoResolveDecision()
            } else if (state.priorityPlayerId != null && (state.stack.isNotEmpty() || state.pendingDecision != null)) {
                bothPass()
            }
        }
        return scrys
    }

    test("two Rabbits entering simultaneously cause exactly ONE scry") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(active, "Valley Questcaller")

        driver.giveMana(active, Color.WHITE, 1)
        val spell = driver.putCardInHand(active, "Test Rabbit Pair")
        driver.castSpell(active, spell)
        driver.bothPass() // resolve the sorcery → both tokens enter at once → one batched trigger

        val scrys = driver.resolveAllCountingScrys(active)
        scrys shouldBe 1
    }

    test("Questcaller entering alone does not trigger its own scry") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.giveMana(active, Color.WHITE, 2)
        val questcaller = driver.putCardInHand(active, "Valley Questcaller")
        driver.castSpell(active, questcaller)
        driver.bothPass() // resolve the creature spell — it is itself a Rabbit, but "other"

        val scrys = driver.resolveAllCountingScrys(active)
        scrys shouldBe 0
    }
})
