package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scarecrow (DRK #108) — "{6}, {T}: Prevent all damage that would be dealt to you this turn by
 * creatures with flying."
 *
 * The shield names a *player* as its recipient and narrows the *source* side to a group, which is
 * the shape [com.wingedsheep.sdk.dsl.Effects.PreventAllDamageToYouFrom] expresses: the recipient
 * half of the group shield is empty and only `recipientGroupIncludesController` is set.
 *
 * What these tests pin:
 *   - combat damage from a flying attacker to you is prevented,
 *   - a non-flying attacker is unaffected — the source filter really filters,
 *   - a non-creature source (Lightning Bolt) is unaffected,
 *   - only *you* are protected: a flier's combat damage to your own creature still kills it.
 *
 * Scarecrow is activated during the opponent's turn, because the shield lasts only "this turn"
 * and the flying attacker is theirs.
 */
class ScarecrowScenarioTest : ScenarioTestBase() {

    private val scarecrowAbilityId by lazy {
        cardRegistry.getCard("Scarecrow")!!.activatedAbilities[0].id
    }

    init {
        context("Scarecrow — prevent all damage dealt to you by creatures with flying") {
            test("a flying attacker deals you no combat damage") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Scarecrow", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withCardOnBattlefield(2, "Serra Angel", summoningSickness = false) // 4/4 flying
                    .withActivePlayer(2)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val scarecrow = game.findPermanent("Scarecrow")!!
                val activation = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = scarecrow, abilityId = scarecrowAbilityId)
                )
                withClue("Activating Scarecrow should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                val shieldBadge = game.getClientState(1).players
                    .single { it.playerId == game.player1Id }
                    .activeEffects.single { it.effectId.startsWith("prevent_damage_to_controller_") }
                withClue("Scarecrow's active shield should be visible beside its controller") {
                    shieldBadge.name shouldBe "Damage Shield"
                    shieldBadge.description shouldBe
                        "All damage that would be dealt to you from creature with flying is prevented"
                }

                val before = game.getLifeTotal(1)
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Serra Angel" to 1)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers()
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("All 4 damage from the flying attacker is prevented") {
                    game.getLifeTotal(1) shouldBe before
                }
            }

            test("a non-flying attacker still hits you for full") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Scarecrow", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false) // 2/2, no flying
                    .withActivePlayer(2)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val scarecrow = game.findPermanent("Scarecrow")!!
                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = scarecrow, abilityId = scarecrowAbilityId)
                ).error shouldBe null
                game.resolveStack()

                val before = game.getLifeTotal(1)
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 1)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers()
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("The shield only covers flying sources — the ground attacker connects") {
                    game.getLifeTotal(1) shouldBe before - 2
                }
            }

            test("a non-creature source is unaffected") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Scarecrow", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(2)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val scarecrow = game.findPermanent("Scarecrow")!!
                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = scarecrow, abilityId = scarecrowAbilityId)
                ).error shouldBe null
                game.resolveStack()

                val before = game.getLifeTotal(1)
                game.castSpellTargetingPlayer(1, "Lightning Bolt", 1).error shouldBe null
                game.resolveStack()

                withClue("Lightning Bolt is not a creature with flying — its damage is not prevented") {
                    game.getLifeTotal(1) shouldBe before - 3
                }
            }

            test("your own creatures are not protected — only you") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Scarecrow", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withCardOnBattlefield(1, "Bog Imp", summoningSickness = false) // 1/1 flying blocker
                    .withCardOnBattlefield(2, "Serra Angel", summoningSickness = false) // 4/4 flying
                    .withActivePlayer(2)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val scarecrow = game.findPermanent("Scarecrow")!!
                game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = scarecrow, abilityId = scarecrowAbilityId)
                ).error shouldBe null
                game.resolveStack()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Serra Angel" to 1)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(mapOf("Bog Imp" to listOf("Serra Angel")))
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("The shield names you, not your permanents — the blocker still dies") {
                    game.isOnBattlefield("Bog Imp") shouldBe false
                }
            }
        }
    }
}
