package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dst.cards.VoltaicConstruct
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Voltaic Construct (DST #156) — {4} 2/2 Artifact Creature — Golem Construct,
 * "{2}: Untap target **artifact creature**."
 *
 * "Artifact creature" is a conjunction. The card shipped filtering on
 * `TargetFilter.CreatureOrArtifact` — a disjunction — so it could untap any creature *or* any
 * artifact, a strictly larger set than the printed text allows. A generated render that read "or"
 * into a noun phrase that has none. Found by the Argentum Assay differential gate, which read the
 * sentence and diffed its reading against the committed definition.
 *
 * The two negative tests are the half that fails without the fix: a plain creature and a
 * noncreature artifact each satisfy one predicate and must still be illegal (CR 115.4 — an illegal
 * target can't be chosen).
 */
class VoltaicConstructScenarioTest : FunSpec({

    val abilityId = VoltaicConstruct.activatedAbilities.first().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Forest" to 40))
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** The construct plus the two mana its ability costs. Its cost has no {T}, so no haste needed. */
    fun construct(d: GameTestDriver): EntityId {
        val source = d.putCreatureOnBattlefield(d.player1, "Voltaic Construct")
        repeat(2) { d.putLandOnBattlefield(d.player1, "Forest") }
        return source
    }

    test("it untaps an artifact creature") {
        val d = driver()
        val source = construct(d)

        // A second Voltaic Construct is an artifact creature, which is the only thing this reads.
        val target = d.putCreatureOnBattlefield(d.player1, "Voltaic Construct")
        d.tapPermanent(target)

        d.submit(
            ActivateAbility(
                playerId = d.player1,
                sourceId = source,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(target)),
            )
        ).isSuccess shouldBe true
        d.bothPass()

        d.state.getEntity(target)?.has<TappedComponent>() shouldBe false
    }

    // The regression. `CreatureOrArtifact` made this legal: a Bear is a creature, and half of a
    // disjunction is enough.
    test("a creature that is not an artifact is not a legal target") {
        val d = driver()
        val source = construct(d)

        val bear = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        d.tapPermanent(bear)

        d.submit(
            ActivateAbility(
                playerId = d.player1,
                sourceId = source,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(bear)),
            )
        ).isSuccess shouldBe false
    }

    // …and the other half of the same disjunction.
    test("an artifact that is not a creature is not a legal target") {
        val d = driver()
        val source = construct(d)

        val ingot = d.putPermanentOnBattlefield(d.player1, "Darksteel Ingot")
        d.tapPermanent(ingot)

        d.submit(
            ActivateAbility(
                playerId = d.player1,
                sourceId = source,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Permanent(ingot)),
            )
        ).isSuccess shouldBe false
    }
})
