package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mir.cards.DwarvenMiner
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Dwarven Miner (MIR #169) — {1}{R} 1/2 Creature — Dwarf,
 * "{2}{R}, {T}: Destroy target **nonbasic** land."
 *
 * The card shipped filtering on `TargetFilter.Land`, so it destroyed basic lands too — a generated
 * render that dropped "nonbasic" while its own `oracleText` field kept it. Found by the Argentum
 * Assay differential gate, which read the printed sentence and diffed the reading against the
 * committed definition.
 *
 * The negative test is the half that fails without the fix (CR 115.4 — an illegal target can't be
 * chosen).
 */
class DwarvenMinerScenarioTest : FunSpec({

    val abilityId = DwarvenMiner.activatedAbilities.first().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** The miner, untapped and able to pay {2}{R} — its cost includes {T}, so the sickness matters. */
    fun miner(d: GameTestDriver): EntityId {
        val source = d.putCreatureOnBattlefield(d.player1, "Dwarven Miner")
        d.removeSummoningSickness(source)
        repeat(3) { d.putLandOnBattlefield(d.player1, "Mountain") }
        return source
    }

    test("it destroys a nonbasic land") {
        val d = driver()
        val source = miner(d)
        val gate = d.putLandOnBattlefield(d.player2, "Azorius Guildgate")

        d.findPermanent(d.player2, "Azorius Guildgate").shouldNotBeNull()
        d.submit(
            ActivateAbility(
                playerId = d.player1,
                sourceId = source,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(gate)),
            )
        ).isSuccess shouldBe true
        while (d.stackSize > 0) d.bothPass()

        d.findPermanent(d.player2, "Azorius Guildgate").shouldBeNull()
    }

    // The regression the gate found: with "nonbasic" dropped, a Mountain was a legal target and the
    // miner could strip a basic-land mana base.
    test("a basic land is not a legal target") {
        val d = driver()
        val source = miner(d)
        val mountain = d.putLandOnBattlefield(d.player2, "Mountain")

        d.submit(
            ActivateAbility(
                playerId = d.player1,
                sourceId = source,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(mountain)),
            )
        ).isSuccess shouldBe false

        d.findPermanent(d.player2, "Mountain").shouldNotBeNull()
    }
})
