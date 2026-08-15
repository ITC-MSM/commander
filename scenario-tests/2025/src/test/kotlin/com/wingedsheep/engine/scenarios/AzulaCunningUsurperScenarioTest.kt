package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.permissions.MayPlayPermission
import com.wingedsheep.engine.state.permissions.addMayPlayPermission
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.conditions.IsYourTurn
import com.wingedsheep.sdk.scripting.effects.ManaExpiry
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Azula, Cunning Usurper (TLA) — {2}{U}{B}{B} Legendary Creature — Human Noble Rogue, 4/4.
 *
 * - Firebending 2 (reused keyword) — asserted via attack → two red combat-duration mana.
 * - ETB: "target opponent exiles a nontoken creature they control, then they exile a nonland card
 *   from their graveyard." The opponent makes both choices; both cards are exiled *with Azula*
 *   (linked exile) and a play permission is granted.
 * - "During your turn, you may cast cards exiled with Azula … as though they had flash. Mana of any
 *   type can be spent." — the new `asThoughFlash` rider + existing `withAnyManaType`, gated to your
 *   turn by `IsYourTurn`.
 */
class AzulaCunningUsurperScenarioTest : ScenarioTestBase() {

    /** Drive every ETB decision: target the opponent, then let the opponent pick each exile. */
    private fun TestGame.resolveEtb(opponentId: EntityId) {
        var guard = 0
        while ((getPendingDecision() != null || state.stack.isNotEmpty()) && guard++ < 60) {
            when (val d = getPendingDecision()) {
                is ChooseTargetsDecision -> {
                    val legal = d.legalTargets[0].orEmpty()
                    // "target opponent" — pick the opponent (the only legal player target).
                    val pick = if (opponentId in legal) listOf(opponentId) else legal.take(1)
                    selectTargets(pick)
                }
                is SelectCardsDecision -> selectCards(d.options.take(d.minSelections.coerceAtLeast(1)))
                is SelectManaSourcesDecision -> submitManaSourcesAutoPay()
                null -> resolveStack()
                else -> error("Unexpected decision in Azula ETB: $d")
            }
        }
    }

    init {
        test("firebending 2 — attacking adds two red combat-duration mana") {
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Azula, Cunning Usurper")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Azula, Cunning Usurper" to 2))
            game.resolveStack()

            val combatMana = game.state.getEntity(game.player1Id)
                ?.get<ManaPoolComponent>()
                ?.restrictedMana
                ?.filter { it.expiry == ManaExpiry.END_OF_COMBAT }
                ?: emptyList()
            withClue("Firebending 2 adds exactly two red combat-duration mana on attack") {
                combatMana shouldHaveSize 2
            }
        }

        test("ETB — opponent exiles a nontoken creature and a nonland graveyard card, both linked to Azula") {
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withCardInHand(1, "Azula, Cunning Usurper")
                .withLandsOnBattlefield(1, "Island", 3)
                .withLandsOnBattlefield(1, "Swamp", 2)
                // Opponent controls one eligible nontoken creature + one ineligible token creature.
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant", isToken = true)
                // Opponent's graveyard holds one eligible nonland card + one ineligible land.
                .withCardInGraveyard(2, "Lightning Bolt")
                .withCardInGraveyard(2, "Forest")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val castResult = game.castSpell(1, "Azula, Cunning Usurper")
            castResult.error shouldBe null

            game.resolveEtb(game.player2Id)

            val azula = game.findPermanent("Azula, Cunning Usurper")
            azula.shouldNotBeNull()

            // Both chosen cards are exiled with Azula (a linked exile).
            val linked = game.state.getEntity(azula)?.get<LinkedExileComponent>()?.exiledIds ?: emptyList()
            withClue("Azula's linked-exile pile holds exactly the two exiled cards") {
                linked shouldHaveSize 2
            }
            val linkedNames = linked.mapNotNull { game.state.getEntity(it)?.get<CardComponent>()?.name }.toSet()
            linkedNames shouldBe setOf("Grizzly Bears", "Lightning Bolt")

            // Ineligible objects are untouched: the token creature stays, the land stays in the graveyard.
            withClue("The token creature is not a legal exile choice") {
                game.isOnBattlefield("Hill Giant") shouldBe true
                game.isOnBattlefield("Grizzly Bears") shouldBe false
            }
            withClue("A land in the graveyard is not a legal exile choice") {
                game.state.getGraveyard(game.player2Id)
                    .any { game.state.getEntity(it)?.get<CardComponent>()?.name == "Forest" } shouldBe true
            }

            // The play grant is created: instant-speed ("as though they had flash"), any mana, your-turn only.
            val perm = game.state.mayPlayPermissions.firstOrNull { it.controllerId == game.player1Id }
            perm.shouldNotBeNull()
            withClue("Grant is flash-timed, any-mana, and gated to your turn") {
                perm.asThoughFlash shouldBe true
                perm.withAnyManaType shouldBe true
                perm.condition shouldBe IsYourTurn
                perm.cardIds shouldBe linked.toSet()
            }
        }

        test("cast from exile — a card exiled with Azula is castable at instant speed paying off-color mana on your turn") {
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Azula, Cunning Usurper")
                .withCardInExile(2, "Grizzly Bears") // {1}{G} creature exiled with Azula
                .withLandsOnBattlefield(1, "Island", 2) // only off-color (blue) mana available
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val azula = game.findPermanent("Azula, Cunning Usurper")!!
            val grizzly = game.state.getExile(game.player2Id)
                .first { game.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears" }

            val seeded = game.state.newEntity()
            game.state = seeded.second
                .updateEntity(azula) { it.with(LinkedExileComponent(listOf(grizzly))) }
                .addMayPlayPermission(
                    MayPlayPermission(
                        id = seeded.first,
                        cardIds = setOf(grizzly),
                        controllerId = game.player1Id,
                        sourceId = azula,
                        condition = IsYourTurn,
                        withAnyManaType = true,
                        asThoughFlash = true,
                        permanent = true,
                        timestamp = seeded.second.timestamp
                    )
                )

            // Reach the beginning-of-combat step through real game flow. It is a combat (non-main)
            // step, so sorcery-speed timing is unavailable and only the flash rider unlocks the cast;
            // reaching it via priority (rather than jumping into declare-attackers) keeps a valid
            // priority window so the stack can actually resolve.
            game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
            game.state.phase shouldBe Phase.COMBAT

            // Cast the exiled green creature paying {1}{G} entirely with blue mana at instant speed.
            val result = game.execute(CastSpell(game.player1Id, grizzly, paymentStrategy = PaymentStrategy.AutoPay))
            withClue("The flash rider + any-mana grant lets the exiled creature be cast at instant speed") {
                result.error shouldBe null
            }

            // Drive the cast to resolution — auto-pay any mana-source prompt, then resolve the stack.
            var guard = 0
            while ((game.getPendingDecision() != null || game.state.stack.isNotEmpty()) && guard++ < 40) {
                when (game.getPendingDecision()) {
                    is SelectManaSourcesDecision -> game.submitManaSourcesAutoPay()
                    null -> game.resolveStack()
                    else -> error("Unexpected decision casting from exile: ${game.getPendingDecision()}")
                }
            }

            val entered = game.findPermanent("Grizzly Bears")
            entered.shouldNotBeNull()
            withClue("The exiled green creature resolved onto the battlefield under player1 (blue mana paid its green pip)") {
                game.state.projectedState.getController(entered) shouldBe game.player1Id
            }
        }

        test("cast from exile — the exiled card is not castable on an opponent's turn") {
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withCardOnBattlefield(1, "Azula, Cunning Usurper")
                .withCardInExile(2, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Island", 2)
                .withActivePlayer(2) // opponent's turn
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .withPriorityPlayer(1)
                .build()

            val azula = game.findPermanent("Azula, Cunning Usurper")!!
            val grizzly = game.state.getExile(game.player2Id)
                .first { game.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears" }

            val seeded = game.state.newEntity()
            game.state = seeded.second
                .updateEntity(azula) { it.with(LinkedExileComponent(listOf(grizzly))) }
                .addMayPlayPermission(
                    MayPlayPermission(
                        id = seeded.first,
                        cardIds = setOf(grizzly),
                        controllerId = game.player1Id,
                        sourceId = azula,
                        condition = IsYourTurn,
                        withAnyManaType = true,
                        asThoughFlash = true,
                        permanent = true,
                        timestamp = seeded.second.timestamp
                    )
                )

            val result = game.execute(CastSpell(game.player1Id, grizzly))
            withClue("The IsYourTurn gate closes the permission on the opponent's turn") {
                result.error shouldNotBe null
            }
        }
    }
}
