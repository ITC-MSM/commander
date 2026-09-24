package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.StateBasedActionChecker
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * CR 704.5n: "If an Equipment ... is attached to an illegal permanent ..., it becomes
 * unattached from that permanent. It remains on the battlefield."
 *
 * An Equipment can only be attached to a creature (CR 301.5). So once the host stops being
 * a creature while the Equipment is still attached, the attachment is illegal and the
 * state-based action must unattach it. This arises whenever a legally-equipped creature
 * becomes a non-creature: e.g. it's turned into a land (Song of the Dryads), or it was an
 * animated artifact whose "until end of turn" animation wore off while equipped.
 *
 * Here we model the resulting board directly — Atomic Microsizer (Equipment) attached to a
 * non-creature permanent (a Plains, standing in for the host that lost its creature type) —
 * and assert the SBA unattaches it. The complementary case (the Equipment *itself* becoming
 * a creature) is covered by [EquipmentAsCreatureUnattachTest].
 */
class EquipmentHostNotCreatureUnattachTest : ScenarioTestBase() {

    init {
        context("Equipment attached to a non-creature host -> SBA must unattach (CR 704.5n)") {

            test("Atomic Microsizer attached to a land auto-unattaches as a state-based action") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Plains")
                    .withCardOnBattlefield(1, "Atomic Microsizer", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val plains = game.findPermanent("Plains")!!
                val microsizer = game.findPermanent("Atomic Microsizer")!!

                // Attach Microsizer to the (non-creature) land, simulating a host that was a
                // legal creature at equip time but has since stopped being a creature.
                game.state = game.state
                    .updateEntity(microsizer) { c -> c.with(AttachedToComponent(plains)) }
                    .updateEntity(plains) { c ->
                        val existing = c.get<AttachmentsComponent>()?.attachedIds ?: emptyList()
                        c.with(AttachmentsComponent(existing + microsizer))
                    }

                withClue("precondition: Microsizer is attached to the land before SBAs run") {
                    game.state.getEntity(microsizer)?.get<AttachedToComponent>()?.targetId shouldBe plains
                }

                // Run state-based actions over the board.
                val sbaChecker = StateBasedActionChecker(zones, cardRegistry = cardRegistry)
                game.state = sbaChecker.checkAndApply(game.state).newState

                withClue(
                    "Microsizer should be unattached from the non-creature host (CR 704.5n) " +
                        "(observed AttachedToComponent.targetId = " +
                        "${game.state.getEntity(microsizer)?.get<AttachedToComponent>()?.targetId})"
                ) {
                    game.state.getEntity(microsizer)?.has<AttachedToComponent>() shouldBe false
                }

                withClue(
                    "the land's reverse AttachmentsComponent link should be cleaned up " +
                        "(observed = " +
                        "${game.state.getEntity(plains)?.get<AttachmentsComponent>()?.attachedIds})"
                ) {
                    val attached = game.state.getEntity(plains)
                        ?.get<AttachmentsComponent>()?.attachedIds ?: emptyList()
                    attached.contains(microsizer) shouldBe false
                }
            }
        }
    }
}
