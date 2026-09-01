package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.GolgariGuildmage
import com.wingedsheep.mtg.sets.definitions.rav.cards.Reroute
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Reroute — {1}{R} Instant (Ravnica: City of Guilds #139)
 *
 * "Change the target of target activated ability with a single target. Draw a card."
 *
 * The opponent's Golgari Guildmage aims its "{4}{G}: put a +1/+1 counter on target creature"
 * at their own creature; Reroute redirects it onto ours and draws. The second case checks the
 * redirect only offers targets the ability's own requirement allows, and the third that a spell
 * on the stack is not a legal Reroute target.
 */
class RerouteScenarioTest : FunSpec({

    val counterAbility = GolgariGuildmage.activatedAbilities[1].id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + Reroute + GolgariGuildmage)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    /** The opponent activates the Guildmage at [target] and passes priority back to us. */
    fun GameTestDriver.opponentPumps(mage: EntityId, target: EntityId): EntityId {
        val opponent = getOpponent(player1)
        giveMana(opponent, Color.GREEN, 1)
        giveColorlessMana(opponent, 4)
        passPriority(player1)
        submit(ActivateAbility(opponent, mage, counterAbility, targets = listOf(ChosenTarget.Permanent(target))))
            .isSuccess shouldBe true
        val ability = getTopOfStack()!!
        passPriority(opponent)
        return ability
    }

    fun GameTestDriver.rerouteInHand(): EntityId {
        giveMana(player1, Color.RED, 1)
        giveColorlessMana(player1, 1)
        return putCardInHand(player1, "Reroute")
    }

    test("redirects an activated ability onto our creature and draws a card") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)
        val mage = d.putCreatureOnBattlefield(opponent, "Golgari Guildmage")
        val theirs = d.putCreatureOnBattlefield(opponent, "Savannah Lions")
        val ours = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")

        val ability = d.opponentPumps(mage, theirs)
        val reroute = d.rerouteInHand()
        val handBefore = d.getHandSize(d.player1)
        d.castSpellWithTargets(d.player1, reroute, listOf(ChosenTarget.Spell(ability))).error shouldBe null
        d.bothPass()

        withClue("Reroute asks for the new target") {
            d.state.pendingDecision shouldNotBe null
        }
        d.submitCardSelection(d.player1, listOf(ours)).error shouldBe null
        withClue("Reroute drew a card (its own cast is already out of hand)") {
            d.getHandSize(d.player1) shouldBe handBefore
        }

        d.bothPass()
        withClue("the counter landed on our creature, not theirs") {
            d.plusOneCounters(ours) shouldBe 1
            d.plusOneCounters(theirs) shouldBe 0
        }
    }

    test("the new target must satisfy the ability's own requirement — a player is not offered") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)
        val mage = d.putCreatureOnBattlefield(opponent, "Golgari Guildmage")
        val theirs = d.putCreatureOnBattlefield(opponent, "Savannah Lions")

        val ability = d.opponentPumps(mage, theirs)
        val reroute = d.rerouteInHand()
        d.castSpellWithTargets(d.player1, reroute, listOf(ChosenTarget.Spell(ability))).error shouldBe null
        d.bothPass()

        d.state.pendingDecision shouldNotBe null
        withClue("a player is never a legal target for 'target creature'") {
            d.submitCardSelection(d.player1, listOf(d.player1)).error shouldNotBe null
        }
        withClue("the Guildmage itself is the only other creature, so it is the only offer") {
            d.submitCardSelection(d.player1, listOf(mage)).error shouldBe null
        }
        d.bothPass()
        d.plusOneCounters(mage) shouldBe 1
    }

    test("a spell on the stack is not an activated ability and can't be targeted") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)
        d.passPriority(d.player1)
        val bolt = d.putCardInHand(opponent, "Lightning Bolt")
        d.giveMana(opponent, Color.RED, 1)
        d.castSpell(opponent, bolt, targets = listOf(d.player1)).error shouldBe null
        d.passPriority(opponent)

        val reroute = d.rerouteInHand()
        withClue("Reroute can't target a spell") {
            d.castSpellWithTargets(d.player1, reroute, listOf(ChosenTarget.Spell(bolt))).error shouldNotBe null
        }
    }
})
