package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.MirrorEntity
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Mirror Entity (LRW #31) — "{X}: Until end of turn, creatures you control have base power and
 * toughness X/X and gain all creature types."
 *
 * Three things are being proved, none of which is visible in the card's own text:
 *
 *  - **X reaches the group body.** The X paid for the activation has to survive into a
 *    `ForEachInGroup` iteration. If it were lost, every creature would be set to 0/0 and the
 *    board would evaporate — a failure that looks exactly like a legitimate X=0 activation, so
 *    the X=0 case is asserted separately to keep the two distinguishable.
 *  - **It sets a *base*, not a bonus.** A 3/3 taken to X=1 must *shrink*, which is what
 *    separates layer 7b from a `ModifyStats` that would read the same on the card.
 *  - **The changeling half is not decoration.** "Gain all creature types" is the reason this
 *    card is a tribal payoff at all; a script that only set P/T would pass every stat assertion.
 *
 * The X=0 board wipe is the printed behaviour, not a bug: "Activating the ability with X = 0
 * will cause all creatures you control to become 0/0 and be put into the graveyard."
 * (2021-03-19)
 */
class MirrorEntityScenarioTest : FunSpec({

    val entityAbility = MirrorEntity.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + MirrorEntity)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.activate(x: Int): Boolean {
        giveMana(player1, Color.WHITE, maxOf(x, 1))
        val entity = getCreatures(player1).first { getCardName(it) == "Mirror Entity" }
        val result = submit(ActivateAbility(player1, entity, entityAbility, xValue = x))
        bothPass()
        return result.isSuccess
    }

    fun GameTestDriver.power(id: EntityId): Int? = state.projectedState.getPower(id)
    fun GameTestDriver.toughness(id: EntityId): Int? = state.projectedState.getToughness(id)

    test("X=4 sets every creature you control to a base 4/4 and makes them every creature type") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)
        d.putCreatureOnBattlefield(d.player1, "Mirror Entity")
        val giant = d.putCreatureOnBattlefield(d.player1, "Hill Giant")
        val bears = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        val theirs = d.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        d.activate(4) shouldBe true

        withClue("the 2/2 became a base 4/4 — X reached the iteration body") {
            d.power(bears) shouldBe 4
            d.toughness(bears) shouldBe 4
        }
        withClue("the 3/3 also became 4/4") {
            d.power(giant) shouldBe 4
            d.toughness(giant) shouldBe 4
        }
        withClue("a Bear that gained all creature types is now a Goblin too") {
            d.state.projectedState.hasSubtype(bears, "Goblin") shouldBe true
        }
        withClue("the opponent's creature is not one you control") {
            d.power(theirs) shouldBe 2
            d.state.projectedState.hasSubtype(theirs, "Goblin") shouldBe false
        }
    }

    test("X=1 shrinks a 3/3, proving this is a base-setting effect and not a pump") {
        val d = driver()
        d.putCreatureOnBattlefield(d.player1, "Mirror Entity")
        val giant = d.putCreatureOnBattlefield(d.player1, "Hill Giant")

        d.activate(1) shouldBe true

        withClue("a +1/+1 bonus would have made it 4/4; a base set makes it 1/1") {
            d.power(giant) shouldBe 1
            d.toughness(giant) shouldBe 1
        }
    }

    test("X=0 wipes your own board, exactly as the ruling says") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)
        d.putCreatureOnBattlefield(d.player1, "Mirror Entity")
        d.putCreatureOnBattlefield(d.player1, "Hill Giant")
        d.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        d.activate(0) shouldBe true

        withClue("every creature you control became 0/0 and died to state-based actions") {
            d.getCreatures(d.player1).size shouldBe 0
        }
        withClue("the opponent's board is untouched") {
            d.getCreatures(opponent).size shouldBe 1
        }
    }
})
