package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.msh.cards.IronManArmor
import com.wingedsheep.mtg.sets.definitions.msh.cards.RoninShadowStalker
import com.wingedsheep.mtg.sets.definitions.msh.cards.SuperSuit
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Ronin, Shadow Stalker (Marvel Super Heroes #112).
 *
 * {2}{B} · Legendary Creature — Human Rogue Hero · 3/3
 *   Pay 2 life: Add two mana of any one color. Spend this mana only to cast Equipment spells or
 *   activate equip abilities. Activate only once each turn.
 *   {T}, Sacrifice an Equipment attached to Ronin: Target creature gets -4/-4 until end of turn.
 *   Activate only as a sorcery.
 *
 * The mana ability's restriction is the composition
 * `AnyOf(SubtypeSpellsOnly("Equipment"), EquipAbilityActivationOnly)`; the restriction atom's own
 * truth table and the "not any ability of an Equipment" discrimination live in
 * `EquipAbilityManaRestrictionTest`. These cover the card: the life cost, the once-each-turn cap,
 * both halves of the spend clause end-to-end, and the sacrifice ability's source-relative scoping.
 */
class RoninShadowStalkerScenarioTest : ScenarioTestBase() {

    private val manaAbilityId = RoninShadowStalker.activatedAbilities.first { it.isManaAbility }.id
    private val minusAbilityId = RoninShadowStalker.activatedAbilities.first { !it.isManaAbility }.id
    private val armorEquipAbilityId = IronManArmor.activatedAbilities.single { it.isEquipAbility }.id

    private fun addMana(game: TestGame, ronin: EntityId, color: Color = Color.BLUE) =
        game.execute(
            ActivateAbility(
                playerId = game.player1Id,
                sourceId = ronin,
                abilityId = manaAbilityId,
                manaColorChoice = color,
            )
        )

    private fun restrictedMana(game: TestGame) =
        game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.restrictedMana.orEmpty()

    /**
     * Affordability of every enumerated activation of the -4/-4 ability on [ronin]. One entry per
     * offered activation, so an empty list means "never enumerated" and can't be confused with
     * "offered but greyed out" — the ability's cost is a [com.wingedsheep.sdk.scripting.costs.AbilityCost.Composite],
     * which the enumerator lists even when unaffordable.
     */
    private fun minusAbilityAffordability(game: TestGame, ronin: EntityId): List<Boolean> =
        game.getLegalActions(1)
            .mapNotNull { info -> (info.action as? ActivateAbility)?.let { it to info.isAffordable } }
            .filter { (action, _) -> action.sourceId == ronin && action.abilityId == minusAbilityId }
            .map { (_, affordable) -> affordable }

    init {
        cardRegistry.register(RoninShadowStalker)
        cardRegistry.register(IronManArmor)
        cardRegistry.register(SuperSuit)

        context("Ronin, Shadow Stalker — the mana ability") {

            test("pays 2 life and adds two restricted mana of one chosen color") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ronin, Shadow Stalker")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ronin = game.findPermanent("Ronin, Shadow Stalker")!!
                val lifeBefore = game.state.lifeTotal(game.player1Id)

                withClue("activation should succeed") { addMana(game, ronin, Color.RED).error shouldBe null }

                game.state.lifeTotal(game.player1Id) shouldBe lifeBefore - 2
                val floating = restrictedMana(game)
                floating.size shouldBe 2
                floating.all { it.color == Color.RED } shouldBe true
                floating.map { it.restriction }.distinct() shouldBe listOf(
                    ManaRestriction.AnyOf(
                        listOf(
                            ManaRestriction.SubtypeSpellsOnly(setOf("Equipment")),
                            ManaRestriction.EquipAbilityActivationOnly,
                        )
                    )
                )
                // A mana ability doesn't use the stack (CR 605.3b).
                game.state.stack.size shouldBe 0
            }

            test("activates only once each turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ronin, Shadow Stalker")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ronin = game.findPermanent("Ronin, Shadow Stalker")!!
                addMana(game, ronin).error shouldBe null

                val second = addMana(game, ronin)
                withClue("the second activation this turn must be refused") {
                    second.error shouldNotBe null
                }
                restrictedMana(game).size shouldBe 2
            }

            test("the mana casts an Equipment spell but not a nonEquipment artifact spell") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ronin, Shadow Stalker")
                    .withCardInHand(1, "Super Suit")
                    .withCardInHand(1, "Artifact Creature")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ronin = game.findPermanent("Ronin, Shadow Stalker")!!
                addMana(game, ronin, Color.BLUE).error shouldBe null

                // "Artifact Creature" is {2} colorless and not an Equipment — refused even though
                // the pool holds exactly two mana.
                val golem = game.castSpell(1, "Artifact Creature")
                withClue("a nonEquipment spell must not be payable from this mana") {
                    golem.error shouldNotBe null
                }

                // Super Suit is {1}{U} — an Equipment spell, so the same two blue mana pay for it.
                val suit = game.castSpell(1, "Super Suit")
                withClue("an Equipment spell must be payable: ${suit.error}") {
                    suit.error shouldBe null
                }
                restrictedMana(game).size shouldBe 0
            }

            test("the mana pays an equip ability") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ronin, Shadow Stalker")
                    .withCardOnBattlefield(1, "Iron Man Armor")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ronin = game.findPermanent("Ronin, Shadow Stalker")!!
                val armor = game.findPermanent("Iron Man Armor")!!
                addMana(game, ronin).error shouldBe null

                val equip = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = armor,
                        abilityId = armorEquipAbilityId,
                        targets = listOf(ChosenTarget.Permanent(ronin)),
                    )
                )
                withClue("equip must be payable from Ronin's mana: ${equip.error}") {
                    equip.error shouldBe null
                }
            }
        }

        context("Ronin, Shadow Stalker — the sacrifice ability") {

            test("sacrificing an attached Equipment gives target creature -4/-4") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ronin, Shadow Stalker")
                    .withCardAttachedTo(1, "Iron Man Armor", "Ronin, Shadow Stalker")
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ronin = game.findPermanent("Ronin, Shadow Stalker")!!
                val armor = game.findPermanent("Iron Man Armor")!!
                val courser = game.findPermanent("Centaur Courser")!!

                // Enumeration must *offer* the ability as affordable: the source-relative sacrifice
                // filter only finds the attached Armor when the ability's own source reaches the
                // cost enumerator. This is the half the explicit `costPayment` submission below
                // never exercises.
                withClue("the -4/-4 ability must be offered and affordable") {
                    minusAbilityAffordability(game, ronin) shouldBe listOf(true)
                }

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = ronin,
                        abilityId = minusAbilityId,
                        targets = listOf(ChosenTarget.Permanent(courser)),
                        costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(armor)),
                    )
                )
                withClue("activation should succeed: ${result.error}") { result.error shouldBe null }

                // The Equipment is gone as a cost, Ronin is tapped, and the 3/3 dies to -4/-4.
                game.state.getZone(game.player1Id, Zone.GRAVEYARD).contains(armor) shouldBe true
                game.resolveStack()
                game.checkStateBasedActions()
                game.findPermanent("Centaur Courser") shouldBe null
            }

            test("activating with no cost payment auto-picks the only attached Equipment") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ronin, Shadow Stalker")
                    .withCardAttachedTo(1, "Iron Man Armor", "Ronin, Shadow Stalker")
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ronin = game.findPermanent("Ronin, Shadow Stalker")!!
                val armor = game.findPermanent("Iron Man Armor")!!
                val courser = game.findPermanent("Centaur Courser")!!

                // The choice is forced (one attached Equipment, count 1), so the handler doesn't
                // pause — it resolves the candidate itself, which is the source-relative lookup on
                // the payment side rather than the enumeration side.
                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = ronin,
                        abilityId = minusAbilityId,
                        targets = listOf(ChosenTarget.Permanent(courser)),
                    )
                )
                withClue("activation without an explicit cost payment should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                withClue("the attached Equipment was auto-picked and sacrificed") {
                    game.state.getZone(game.player1Id, Zone.GRAVEYARD).contains(armor) shouldBe true
                }
            }

            test("an Equipment attached to another creature can't pay the cost") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ronin, Shadow Stalker")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Iron Man Armor", "Grizzly Bears")
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ronin = game.findPermanent("Ronin, Shadow Stalker")!!
                val armor = game.findPermanent("Iron Man Armor")!!
                val courser = game.findPermanent("Centaur Courser")!!

                // Enumeration greys the ability out — the only Equipment on the battlefield is
                // attached to something else, so the source-relative sacrifice cost has no
                // candidate. Asserted as "listed exactly once, and unaffordable" rather than
                // "nothing affordable", so it can't pass on an empty list.
                withClue("the ability must be listed and unaffordable") {
                    minusAbilityAffordability(game, ronin) shouldBe listOf(false)
                }

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = ronin,
                        abilityId = minusAbilityId,
                        targets = listOf(ChosenTarget.Permanent(courser)),
                        costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(armor)),
                    )
                )
                withClue("submitting it anyway must be refused") { result.error shouldNotBe null }
            }

            test("activates only as a sorcery") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Ronin, Shadow Stalker")
                    .withCardAttachedTo(1, "Iron Man Armor", "Ronin, Shadow Stalker")
                    .withCardOnBattlefield(2, "Centaur Courser")
                    .withActivePlayer(2)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ronin = game.findPermanent("Ronin, Shadow Stalker")!!
                val armor = game.findPermanent("Iron Man Armor")!!
                val courser = game.findPermanent("Centaur Courser")!!

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = ronin,
                        abilityId = minusAbilityId,
                        targets = listOf(ChosenTarget.Permanent(courser)),
                        costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(armor)),
                    )
                )
                withClue("a sorcery-speed ability isn't activatable on the opponent's turn") {
                    result.error shouldNotBe null
                }
            }
        }
    }
}
