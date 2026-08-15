package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.WitherbloomTheBalancer
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Cost-reduction tests for Witherbloom, the Balancer (Secrets of Strixhaven #245).
 *
 * Witherbloom ({6}{B}{G}, 5/5, flying, deathtouch):
 *   Affinity for creatures (this spell costs {1} less to cast for each creature you control).
 *   Instant and sorcery spells you cast have affinity for creatures.
 *
 * Both affinity clauses are ModifySpellCost reductions equal to the number of creatures you
 * control: the keyword on Witherbloom itself (SelfCast), and the grant to your instant/sorcery
 * spells (YouCast InstantOrSorcery). A vanilla creature you cast is NOT reduced (no grant to it).
 */
class WitherbloomTheBalancerScenarioTest : FunSpec({

    val BigInstant = card("Big Instant") {
        manaCost = "{6}{R}"
        typeLine = "Instant"
    }
    val BigCreature = card("Big Creature") {
        manaCost = "{6}{R}"
        typeLine = "Creature — Giant"
        power = 4
        toughness = 4
    }

    fun registry(): CardRegistry = CardRegistry().apply {
        register(TestCards.all)
        register(WitherbloomTheBalancer)
        register(BigInstant)
        register(BigCreature)
    }

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        registerCard(WitherbloomTheBalancer)
        registerCard(BigInstant)
        registerCard(BigCreature)
    }

    test("Witherbloom's own affinity reduces its generic cost by the number of creatures you control") {
        val reg = registry()
        val calc = CostCalculator(reg)
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 20), startingLife = 20)
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Three creatures you control → affinity for creatures shaves 3 generic off {6}{B}{G}.
        d.putCreatureOnBattlefield(me, "Grizzly Bears")
        d.putCreatureOnBattlefield(me, "Grizzly Bears")
        d.putCreatureOnBattlefield(me, "Grizzly Bears")

        val cost = calc.calculateEffectiveCost(d.state, reg.requireCard("Witherbloom, the Balancer"), me)
        cost.genericAmount shouldBe 3 // 6 generic - 3 creatures
        cost.cmc shouldBe 5           // {3}{B}{G}
    }

    test("instant you cast gets affinity for creatures, but a vanilla creature you cast does not") {
        val reg = registry()
        val calc = CostCalculator(reg)
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 20), startingLife = 20)
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Witherbloom, the Balancer")
        d.putCreatureOnBattlefield(me, "Grizzly Bears")
        d.putCreatureOnBattlefield(me, "Grizzly Bears")
        // Witherbloom + two Bears = 3 creatures you control.

        val instant = calc.calculateEffectiveCost(d.state, reg.requireCard("Big Instant"), me)
        instant.genericAmount shouldBe 3 // {6}{R} - 3 creatures = {3}{R}
        instant.cmc shouldBe 4

        val creature = calc.calculateEffectiveCost(d.state, reg.requireCard("Big Creature"), me)
        creature.genericAmount shouldBe 6 // not an instant/sorcery → no reduction
        creature.cmc shouldBe 7
    }
})
