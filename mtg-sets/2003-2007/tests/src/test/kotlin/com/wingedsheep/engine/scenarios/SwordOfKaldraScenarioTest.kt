package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.mrd.cards.ViridianLongbow
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Sword of Kaldra (MRD #251) — "Equipped creature gets +5/+5. Whenever equipped creature deals damage
 * to a creature, exile that creature." Equip {4}.
 *
 * Two claims in the trigger are easy to get backwards, and both are pinned here.
 *
 * **"That creature" is the *damaged* one.** A damage trigger stamps the *recipient* as the triggering
 * entity, so `EffectTarget.TriggeringEntity` is the creature that was hit — not the equipped creature
 * that hit it. The exile landing on the wrong side of the damage would still "work" in a test that
 * only counted exiles.
 *
 * **"Deals damage", not "deals combat damage".** A Viridian Longbow ping is the cleanest proof: it is
 * noncombat damage, and it is only 1, so the target survives the damage itself and can be observed
 * going to exile rather than dying to a state-based action. (The printed reminder "(Exile it only if
 * it's still on the battlefield)" is why a lethal hit needs no wiring — there is nothing left to move.)
 */
class SwordOfKaldraScenarioTest : ScenarioTestBase() {

    // The pinger the Longbow grants its host — the noncombat damage source used below.
    private val longbowPinger =
        ViridianLongbow.staticAbilities.filterIsInstance<GrantActivatedAbility>().single().ability.id

    init {
        context("Sword of Kaldra — exile whatever the equipped creature damages") {
            test("a 1-damage ping exiles the damaged creature and leaves the equipped one alone") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Sword of Kaldra", "Grizzly Bears")
                    .withCardAttachedTo(1, "Viridian Longbow", "Grizzly Bears")
                    .withCardOnBattlefield(2, "Centaur Courser", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val courser = game.findPermanent("Centaur Courser")!!

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = bears,
                        abilityId = longbowPinger,
                        targets = listOf(ChosenTarget.Permanent(courser)),
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("1 damage to a 3/3 is survivable — it left the battlefield by exile, not by dying") {
                    game.isOnBattlefield("Centaur Courser") shouldBe false
                    game.isInExile(2, "Centaur Courser") shouldBe true
                    game.isInGraveyard(2, "Centaur Courser") shouldBe false
                }
                withClue("'that creature' is the damage's recipient, not its source") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
            }

            test("damage dealt to a player does not trigger it") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Sword of Kaldra", "Grizzly Bears")
                    .withCardAttachedTo(1, "Viridian Longbow", "Grizzly Bears")
                    .withCardOnBattlefield(2, "Centaur Courser", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = bears,
                        abilityId = longbowPinger,
                        targets = listOf(ChosenTarget.Player(game.player2Id)),
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("RecipientFilter.AnyCreature — a face ping is not a creature") {
                    game.getLifeTotal(2) shouldBe 19
                    game.isOnBattlefield("Centaur Courser") shouldBe true
                    game.isInExile(2, "Centaur Courser") shouldBe false
                }
            }

            test("equipped creature gets +5/+5") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardAttachedTo(1, "Sword of Kaldra", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val projected = StateProjector().project(game.state)

                projected.getPower(bears) shouldBe 7
                projected.getToughness(bears) shouldBe 7
            }
        }
    }
}
