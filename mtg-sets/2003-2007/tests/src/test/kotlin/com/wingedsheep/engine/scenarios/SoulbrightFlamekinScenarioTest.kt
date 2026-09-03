package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.SoulbrightFlamekin
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Soulbright Flamekin (LRW #190) — "{2}: Target creature gains trample until end of turn. If this
 * is the third time this ability has resolved this turn, you may add {R}{R}{R}{R}{R}{R}{R}{R}."
 *
 * Two claims, and both are about the *third* resolution rather than the trample:
 *
 *  - the mana arrives only on the third resolution (the condition is an equality — the printed
 *    ruling says the fourth and later resolutions get nothing), and
 *  - it is a real "you may", so declining leaves the pool alone. Eight red mana that must be
 *    spent this step is often a liability, so a grant that skipped the prompt would be a bug the
 *    card text does not permit.
 *
 * The ability targets, so it is not a mana ability and must go on the stack — the test resolves
 * it through `bothPass()` for exactly that reason.
 */
class SoulbrightFlamekinScenarioTest : FunSpec({

    val flareAbility = SoulbrightFlamekin.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(SoulbrightFlamekin))
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun handPriorityTo(d: GameTestDriver, player: EntityId) {
        d.priorityPlayer?.takeIf { it != player }?.let { d.passPriority(it) }
    }

    fun redInPool(d: GameTestDriver, player: EntityId): Int =
        d.state.getEntity(player)?.get<ManaPoolComponent>()?.red ?: 0

    /** Activate targeting [victim] and resolve. Returns after priority is back with [me]. */
    fun flare(d: GameTestDriver, me: EntityId, flamekin: EntityId, victim: EntityId) {
        handPriorityTo(d, me)
        d.giveColorlessMana(me, 2)
        d.submit(
            ActivateAbility(
                playerId = me,
                sourceId = flamekin,
                abilityId = flareAbility,
                targets = listOf(entityIdToChosenTarget(d.state, victim))
            )
        ).isSuccess shouldBe true
        d.bothPass()
    }

    test("the third resolution offers eight red mana; accepting fills the pool") {
        val d = driver()
        val me = d.activePlayer!!
        val flamekin = d.putCreatureOnBattlefield(me, "Soulbright Flamekin")
        val courser = d.putCreatureOnBattlefield(me, "Centaur Courser")

        flare(d, me, flamekin, courser)
        withClue("trample lands on the target every time") {
            d.state.projectedState.hasKeyword(courser, Keyword.TRAMPLE) shouldBe true
        }
        withClue("no prompt and no mana on the first resolution") {
            d.isPaused shouldBe false
            redInPool(d, me) shouldBe 0
        }

        flare(d, me, flamekin, courser)
        withClue("still nothing on the second — the condition is an equality, not a threshold") {
            d.isPaused shouldBe false
            redInPool(d, me) shouldBe 0
        }

        flare(d, me, flamekin, courser)
        withClue("the third resolution asks") { d.isPaused shouldBe true }
        d.submitYesNo(me, true).isSuccess shouldBe true

        withClue("\"you may add {R}{R}{R}{R}{R}{R}{R}{R}\"") {
            redInPool(d, me) shouldBe 8
        }
    }

    test("declining the third resolution's offer adds no mana") {
        val d = driver()
        val me = d.activePlayer!!
        val flamekin = d.putCreatureOnBattlefield(me, "Soulbright Flamekin")
        val courser = d.putCreatureOnBattlefield(me, "Centaur Courser")

        repeat(2) { flare(d, me, flamekin, courser) }
        flare(d, me, flamekin, courser)

        d.isPaused shouldBe true
        d.submitYesNo(me, false).isSuccess shouldBe true

        withClue("a declined \"may\" leaves the pool empty") {
            redInPool(d, me) shouldBe 0
        }
        withClue("the trample half happened regardless") {
            d.state.projectedState.hasKeyword(courser, Keyword.TRAMPLE) shouldBe true
        }
    }
})
