package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mrd.cards.PearlShard
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Pearl Shard (MRD #225) — "{3}, {T} or {W}, {T}: Prevent the next 2 damage that would be dealt to
 * any target this turn."
 *
 * "Any target" means a player as readily as a creature, and "the next 2" means a shield that absorbs
 * partially and then runs out — a prevent-all reading would look identical against a single small
 * hit. Both are pinned here, along with the shared `{T}` that makes the two printed cost halves
 * mutually exclusive per untap (2004-10-04 ruling).
 */
class PearlShardScenarioTest : FunSpec({

    val genericAbility = PearlShard.activatedAbilities.first().id
    val whiteAbility = PearlShard.activatedAbilities[1].id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + PearlShard)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.shard(): EntityId = putPermanentOnBattlefield(player1, "Pearl Shard")

    /** Bolt [victim] for 3 from player2, handing them priority first (they are not the active player). */
    fun GameTestDriver.boltAt(victim: EntityId) {
        val bolt = putCardInHand(player2, "Lightning Bolt")
        giveMana(player2, Color.RED, 1)
        passPriority(player1)
        castSpell(player2, bolt, targets = listOf(victim)).isSuccess shouldBe true
        bothPass()
    }

    test("a shield on a player absorbs 2 of a 3-damage burn spell") {
        val d = driver()
        val shard = d.shard()
        d.giveColorlessMana(d.player1, 3)

        d.submit(
            ActivateAbility(d.player1, shard, genericAbility, targets = listOf(ChosenTarget.Player(d.player1)))
        ).isSuccess shouldBe true
        d.bothPass()

        d.boltAt(d.player1)

        withClue("2 of the 3 prevented, so exactly 1 gets through") {
            d.getLifeTotal(d.player1) shouldBe 19
        }
    }

    test("a shield on a creature keeps a 2-damage hit from killing it") {
        val d = driver()
        val shard = d.shard()
        val bear = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears") // 2/2
        d.giveMana(d.player1, Color.WHITE, 1)

        d.submit(
            ActivateAbility(d.player1, shard, whiteAbility, targets = listOf(ChosenTarget.Permanent(bear)))
        ).isSuccess shouldBe true
        d.bothPass()

        d.boltAt(bear)

        withClue("3 damage minus the 2 prevented is 1 — not lethal to a 2/2") {
            d.state.getBattlefield().contains(bear) shouldBe true
        }
    }

    test("only one of the two costs can be paid per untap — both include the shard's {T}") {
        val d = driver()
        val shard = d.shard()
        d.giveColorlessMana(d.player1, 3)
        d.giveMana(d.player1, Color.WHITE, 1)

        d.submit(
            ActivateAbility(d.player1, shard, genericAbility, targets = listOf(ChosenTarget.Player(d.player1)))
        ).isSuccess shouldBe true

        withClue("the shard is now tapped, so the {W} half has no {T} left to pay") {
            d.submit(
                ActivateAbility(d.player1, shard, whiteAbility, targets = listOf(ChosenTarget.Player(d.player1)))
            ).isSuccess shouldBe false
        }
    }
})
