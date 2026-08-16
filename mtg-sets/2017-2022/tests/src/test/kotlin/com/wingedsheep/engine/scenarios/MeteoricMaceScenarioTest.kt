package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.cmr.cards.MeteoricMace
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Meteoric Mace (CMR #192) — {4}{R}{R} Artifact — Equipment
 *
 *   Equipped creature gets +4/+0 and has trample.
 *   Equip {4}
 *   Cascade
 *
 * The corpus' first **cascade on a noncreature permanent spell**. Cascade (CR 702.85a) is itself a
 * "when you cast this spell" triggered ability, so the card wires it as
 * `Triggers.WhenYouCastThisSpell()` -> `Effects.Cascade`; the executor reads the *triggering spell's*
 * mana value for the "costs less" threshold, which is the part that has to work off an Equipment
 * rather than a creature. The library is stacked deterministically here so the cascade hit is not
 * left to a shuffle: a land on top (walked past, then bottom-randomized) and a cheaper nonland card
 * beneath it.
 */
class MeteoricMaceScenarioTest : ScenarioTestBase() {

    init {
        context("Meteoric Mace") {

            test("casting it cascades into a cheaper nonland card from the stacked library") {
                var builder = scenario()
                    .withPlayers("Player1", "Opponent")
                    .withCardInHand(1, "Meteoric Mace")
                    .withLandsOnBattlefield(1, "Mountain", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                // Library, top-down: a land cascade must walk past, then Grizzly Bears (MV 2, less
                // than the Mace's MV 6) as the hit, then filler.
                builder = builder.withCardInLibrary(1, "Mountain")
                builder = builder.withCardInLibrary(1, "Grizzly Bears")
                repeat(3) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                game.castSpell(1, "Meteoric Mace").error shouldBe null
                // The cascade trigger sits above the Mace and resolves first, pausing on the
                // "cast it without paying its mana cost?" question.
                game.resolveStack()

                withClue("cascade found a hit and offered the free cast") {
                    game.hasPendingDecision() shouldBe true
                }
                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(true).error shouldBe null
                game.resolveStack()

                withClue("the cascaded card was cast for free and resolved") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
                withClue("the Equipment itself still resolved underneath the cascade") {
                    game.isOnBattlefield("Meteoric Mace") shouldBe true
                }
                withClue("the uncast exiled land went to the bottom of the library, not exile") {
                    game.isInExile(1, "Mountain") shouldBe false
                    game.librarySize(1) shouldBe 4
                }
            }

            test("equip {4} attaches it and grants the equipped creature +4/+0 and trample") {
                val game = scenario()
                    .withPlayers("Player1", "Opponent")
                    .withCardOnBattlefield(1, "Meteoric Mace")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val mace = game.findPermanent("Meteoric Mace")!!
                val bears = game.findPermanent("Grizzly Bears")!!

                withClue("a plain 2/2 without trample before the Mace is attached") {
                    game.state.projectedState.getPower(bears) shouldBe 2
                    game.state.projectedState.getToughness(bears) shouldBe 2
                    game.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe false
                }

                val equip = MeteoricMace.activatedAbilities.single { it.isEquipAbility }
                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = mace,
                        abilityId = equip.id,
                        targets = listOf(ChosenTarget.Permanent(bears))
                    )
                )
                withClue("activating equip {4} should succeed: ${result.error}") { result.error shouldBe null }
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("the Mace is attached to Grizzly Bears") {
                    game.state.getEntity(mace)?.get<AttachedToComponent>()?.targetId shouldBe bears
                }
                withClue("+4/+0 turns the 2/2 into a 6/2") {
                    game.state.projectedState.getPower(bears) shouldBe 6
                    game.state.projectedState.getToughness(bears) shouldBe 2
                }
                withClue("and the equipped creature has trample") {
                    game.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe true
                }
            }
        }
    }
}
