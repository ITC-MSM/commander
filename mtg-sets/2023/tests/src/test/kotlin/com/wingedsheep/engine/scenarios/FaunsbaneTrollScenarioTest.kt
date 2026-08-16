package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.woe.cards.FaunsbaneTroll
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Faunsbane Troll (Wilds of Eldraine #203).
 *
 * {2}{B}{G} · Creature — Troll · 4/4
 *   When this creature enters, create a Monster Role token attached to it.
 *   {1}, Sacrifice an Aura attached to this creature: This creature fights target creature you
 *   don't control. If that creature would die this turn, exile it instead. Activate only as a
 *   sorcery.
 *
 * The sacrifice cost is **source-relative** — `Enchantment.withSubtype("Aura").attachedToSource()`,
 * i.e. `StatePredicate.IsAttachedToSource` — so it only resolves when the cost enumeration and the
 * cost payment both know the ability's own source. Faunsbane Troll and Ronin, Shadow Stalker are
 * the only two cards in the corpus carrying such a cost filter, which is why the enumeration
 * assertion below is the load-bearing one: without the source the ability is offered but
 * permanently greyed out.
 */
class FaunsbaneTrollScenarioTest : ScenarioTestBase() {

    private val fightAbilityId = FaunsbaneTroll.activatedAbilities.single().id

    /**
     * Affordability of every enumerated activation of the fight ability on [troll] — one entry per
     * offered activation, so an empty list means "never enumerated" rather than "greyed out".
     */
    private fun fightAbilityAffordability(game: TestGame, troll: EntityId): List<Boolean> =
        game.getLegalActions(1)
            .mapNotNull { info -> (info.action as? ActivateAbility)?.let { it to info.isAffordable } }
            .filter { (action, _) -> action.sourceId == troll && action.abilityId == fightAbilityId }
            .map { (_, affordable) -> affordable }

    init {
        test("the Monster Role it makes on entry pays for the fight, which exiles the loser") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Faunsbane Troll")
                .withLandsOnBattlefield(1, "Swamp", 2)
                .withLandsOnBattlefield(1, "Forest", 3)
                .withCardOnBattlefield(2, "Centaur Courser")
                .withActivePlayer(1)
                .withPriorityPlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            withClue("casting the Troll should succeed") {
                game.castSpell(1, "Faunsbane Troll").error shouldBe null
            }
            game.resolveStack()

            val troll = game.findPermanent("Faunsbane Troll")!!
            val role = game.findPermanent("Monster Role")
            withClue("the enters trigger creates a Monster Role attached to the Troll") {
                role shouldNotBe null
                game.state.getEntity(role!!)?.get<AttachedToComponent>()?.targetId shouldBe troll
            }

            // The load-bearing assertion: the source-relative sacrifice cost has to find the Role
            // that is attached to *this* Troll, which only happens when enumeration carries the
            // ability's source.
            withClue("the fight ability must be offered and affordable") {
                fightAbilityAffordability(game, troll) shouldBe listOf(true)
            }

            val courser = game.findPermanent("Centaur Courser")!!
            val result = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = troll,
                    abilityId = fightAbilityId,
                    targets = listOf(ChosenTarget.Permanent(courser)),
                )
            )
            withClue("activation should succeed: ${result.error}") { result.error shouldBe null }
            withClue("the Role was sacrificed as a cost") {
                game.findPermanent("Monster Role") shouldBe null
            }

            game.resolveStack()
            game.checkStateBasedActions()

            withClue("the 4/4 Troll's fight kills the 3/3 Courser") {
                game.findPermanent("Centaur Courser") shouldBe null
            }
            withClue("and it is exiled instead of dying") {
                game.isInExile(2, "Centaur Courser") shouldBe true
            }
            withClue("the Troll survives the 3 damage back") {
                game.findPermanent("Faunsbane Troll") shouldNotBe null
            }
        }

        test("an Aura attached to another creature can't pay the cost") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Faunsbane Troll")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardAttachedTo(1, "Holy Strength", "Grizzly Bears")
                .withLandsOnBattlefield(1, "Forest", 1)
                .withCardOnBattlefield(2, "Centaur Courser")
                .withActivePlayer(1)
                .withPriorityPlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val troll = game.findPermanent("Faunsbane Troll")!!
            val courser = game.findPermanent("Centaur Courser")!!

            // Scenario placement fires no enters trigger, so the Troll has no Role; the only Aura
            // in play enchants the Bears. "Attached to this creature" must reject it.
            withClue("the ability must be listed and unaffordable") {
                fightAbilityAffordability(game, troll) shouldBe listOf(false)
            }

            val result = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = troll,
                    abilityId = fightAbilityId,
                    targets = listOf(ChosenTarget.Permanent(courser)),
                )
            )
            withClue("submitting it anyway must be refused") { result.error shouldNotBe null }
        }
    }
}
