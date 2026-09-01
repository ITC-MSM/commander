package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.StasisCell
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Stasis Cell (RAV #66) — "Enchant creature. Enchanted creature doesn't untap during its
 * controller's untap step. {3}{U}: Attach this Aura to target creature."
 *
 * The lock is a granted [AbilityFlag.DOESNT_UNTAP], and the whole point of the second ability is
 * that the lock **moves with the Aura**. An implementation that attached correctly but left the
 * grant computed against the old host would pass a static "the enchanted creature doesn't untap"
 * test and still be wrong, so the assertion that matters is the pair: the new host locked *and*
 * the old host free, read off projected state after one activation.
 */
class StasisCellScenarioTest : FunSpec({

    val moveAbility = StasisCell.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + StasisCell)
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.enchant(caster: EntityId, creature: EntityId): EntityId {
        val aura = putCardInHand(caster, "Stasis Cell")
        giveColorlessMana(caster, 4)
        giveMana(caster, Color.BLUE, 1)
        castSpell(caster, aura, listOf(creature)).isSuccess shouldBe true
        bothPass()
        return aura
    }

    fun GameTestDriver.locked(creature: EntityId): Boolean =
        state.projectedState.hasKeyword(creature, AbilityFlag.DOESNT_UNTAP)

    test("the enchanted creature is locked and nothing else is") {
        val d = driver()
        val victim = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        val bystander = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        d.enchant(d.player1, victim)

        d.locked(victim) shouldBe true
        d.locked(bystander) shouldBe false
    }

    test("moving the Aura moves the lock with it") {
        val d = driver()
        val first = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        val second = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        val aura = d.enchant(d.player1, first)

        d.giveColorlessMana(d.player1, 3)
        d.giveMana(d.player1, Color.BLUE, 1)
        d.submit(
            ActivateAbility(
                playerId = d.player1,
                sourceId = aura,
                abilityId = moveAbility,
                targets = listOf(ChosenTarget.Permanent(second)),
            )
        ).isSuccess shouldBe true
        var guard = 0
        while (d.stackSize > 0 && guard++ < 20) d.bothPass()

        withClue("the new host inherits the lock") { d.locked(second) shouldBe true }
        withClue("the old host is released — the grant is not left computed against it") {
            d.locked(first) shouldBe false
        }
    }

    test("a locked creature stays tapped through its controller's untap step") {
        val d = driver()
        val victim = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        d.enchant(d.player1, victim)
        d.tapPermanent(victim)

        // Player 2's own untap step is the next one after player 1's turn ends.
        d.passPriorityUntil(Step.UPKEEP)
        d.activePlayer shouldBe d.player2

        d.isTapped(victim) shouldBe true
    }
})
