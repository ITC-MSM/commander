package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.msh.cards.ShangChiMasterOfKungFu
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Shang-Chi, Master of Kung Fu (Marvel Super Heroes #187).
 *
 * {1}{G} · Legendary Creature — Human Warrior Hero · 2/2
 *   You may activate abilities of creatures you control as though those creatures had haste.
 *   {T}: Add two mana of any one color. Spend this mana only to activate abilities of creature
 *   sources.
 *
 * The permission itself (the `MAY_ACTIVATE_ABILITIES_AS_THOUGH_HASTY` flag, the shared
 * `SummoningSicknessRules` gate, `{Q}`, the grant leaving play) is covered at engine level in
 * `ActivateAbilitiesAsThoughHastyTest`. These cover the *card*: that its own mana ability is live
 * the turn it enters, that it reaches the other creatures its controller controls, that it grants no
 * attack rights, and that the restricted mana spends exactly as printed.
 */
class ShangChiMasterOfKungFuScenarioTest : ScenarioTestBase() {

    /**
     * A creature with one `{T}` ability (gated by CR 302.6) and one mana-cost ability (never
     * gated), so a single card exercises both the permission and the mana restriction.
     */
    private val dummy = card("Kung Fu Training Dummy") {
        manaCost = "{1}"
        typeLine = "Creature — Human Monk"
        power = 1
        toughness = 1
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.GainLife(1)
            description = "{T}: You gain 1 life."
        }
        activatedAbility {
            cost = Costs.Mana("{1}")
            effect = Effects.GainLife(2)
            description = "{1}: You gain 2 life."
        }
    }

    /**
     * A *non*-creature source with the same `{1}:` ability as [dummy]'s second one. It is the
     * discriminator for the `CardType.CREATURE` half of the mana restriction: identical cost,
     * identical effect, only the source's card type differs.
     */
    private val scroll = card("Kung Fu Wall Scroll") {
        manaCost = "{1}"
        typeLine = "Artifact"
        activatedAbility {
            cost = Costs.Mana("{1}")
            effect = Effects.GainLife(2)
            description = "{1}: You gain 2 life."
        }
    }

    private val shangChiManaAbilityId = ShangChiMasterOfKungFu.activatedAbilities.single().id
    private val dummyTapAbilityId = dummy.activatedAbilities[0].id
    private val dummyManaCostAbilityId = dummy.activatedAbilities[1].id
    private val scrollManaCostAbilityId = scroll.activatedAbilities.single().id

    private fun restrictedMana(game: TestGame) =
        game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.restrictedMana.orEmpty()

    /** Every enumerated activation of [abilityId] on [sourceId]. Empty means "never offered". */
    private fun activationsOf(game: TestGame, sourceId: EntityId, abilityId: AbilityId) =
        game.getLegalActions(1)
            .mapNotNull { it.action as? ActivateAbility }
            .filter { it.sourceId == sourceId && it.abilityId == abilityId }

    init {
        cardRegistry.register(dummy)
        cardRegistry.register(scroll)

        context("Shang-Chi, Master of Kung Fu — the as-though-haste permission") {

            test("its own {T} mana ability is activatable the turn it enters") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Shang-Chi, Master of Kung Fu", summoningSickness = true)
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val shangChi = game.findPermanent("Shang-Chi, Master of Kung Fu")!!

                withClue("'creatures you control' includes Shang-Chi itself") {
                    activationsOf(game, shangChi, shangChiManaAbilityId).size shouldBe 1
                }

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = shangChi,
                        abilityId = shangChiManaAbilityId,
                        manaColorChoice = Color.GREEN,
                    )
                )
                withClue("activation should succeed: ${result.error}") { result.error shouldBe null }

                val floating = restrictedMana(game)
                floating.size shouldBe 2
                floating.all { it.color == Color.GREEN } shouldBe true
                floating.map { it.restriction }.distinct() shouldBe listOf(
                    ManaRestriction.CardTypeSpellsOrAbilitiesOnly(
                        cardType = CardType.CREATURE,
                        allowSpells = false,
                        allowAbilities = true,
                    )
                )
                // A mana ability doesn't use the stack (CR 605.3b).
                game.state.stack.size shouldBe 0
            }

            test("another summoning-sick creature's {T} ability becomes activatable") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Shang-Chi, Master of Kung Fu")
                    .withCardOnBattlefield(1, "Kung Fu Training Dummy", summoningSickness = true)
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dummyId = game.findPermanent("Kung Fu Training Dummy")!!

                activationsOf(game, dummyId, dummyTapAbilityId).size shouldBe 1

                val lifeBefore = game.getLifeTotal(1)
                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = dummyId,
                        abilityId = dummyTapAbilityId,
                    )
                )
                withClue("activation should succeed: ${result.error}") { result.error shouldBe null }
                game.resolveStack()
                game.getLifeTotal(1) shouldBe lifeBefore + 1
            }

            test("without Shang-Chi the same {T} ability is not offered") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Kung Fu Training Dummy", summoningSickness = true)
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val dummyId = game.findPermanent("Kung Fu Training Dummy")!!

                withClue("this is the control — the permission is the only thing that changes") {
                    activationsOf(game, dummyId, dummyTapAbilityId).size shouldBe 0
                }
            }

            test("it grants no attack rights — neither creature can attack the turn it enters") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Shang-Chi, Master of Kung Fu", summoningSickness = true)
                    .withCardOnBattlefield(1, "Kung Fu Training Dummy", summoningSickness = true)
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val offeredAttackers = game.getLegalActions(1)
                    .firstOrNull { it.actionType == "DeclareAttackers" }
                    ?.validAttackers.orEmpty()
                withClue("CR 302.6's attack half is untouched by 'as though those creatures had haste'") {
                    offeredAttackers shouldBe emptyList()
                }

                game.declareAttackers(mapOf("Kung Fu Training Dummy" to 2)).error shouldNotBe null
                game.declareAttackers(mapOf("Shang-Chi, Master of Kung Fu" to 2)).error shouldNotBe null
            }
        }

        context("Shang-Chi, Master of Kung Fu — the restricted mana") {

            test("the mana pays a creature's activated ability") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Shang-Chi, Master of Kung Fu", summoningSickness = true)
                    .withCardOnBattlefield(1, "Kung Fu Training Dummy", summoningSickness = true)
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val shangChi = game.findPermanent("Shang-Chi, Master of Kung Fu")!!
                val dummyId = game.findPermanent("Kung Fu Training Dummy")!!

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = shangChi,
                        abilityId = shangChiManaAbilityId,
                        manaColorChoice = Color.GREEN,
                    )
                ).error shouldBe null

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = dummyId,
                        abilityId = dummyManaCostAbilityId,
                    )
                )
                withClue("a creature source's ability must be payable: ${result.error}") {
                    result.error shouldBe null
                }
                restrictedMana(game).size shouldBe 1
            }

            test("the mana can't pay for a non-creature source's ability") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Shang-Chi, Master of Kung Fu", summoningSickness = true)
                    .withCardOnBattlefield(1, "Kung Fu Wall Scroll")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val shangChi = game.findPermanent("Shang-Chi, Master of Kung Fu")!!
                val scrollId = game.findPermanent("Kung Fu Wall Scroll")!!

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = shangChi,
                        abilityId = shangChiManaAbilityId,
                        manaColorChoice = Color.GREEN,
                    )
                ).error shouldBe null

                // Same {1}: cost the Training Dummy pays with this mana one test above — the only
                // difference is that this source is an Artifact, not a Creature.
                withClue("'abilities of creature sources' — an Artifact's ability is not one") {
                    game.execute(
                        ActivateAbility(
                            playerId = game.player1Id,
                            sourceId = scrollId,
                            abilityId = scrollManaCostAbilityId,
                        )
                    ).error shouldNotBe null
                }
                withClue("a rejected activation must not have spent any of the restricted mana") {
                    restrictedMana(game).size shouldBe 2
                }
                // Deliberately not asserted here: the *enumerator* does still offer this activation.
                // `ManaSolver`'s affordability pass doesn't carry the ability source's card type, so
                // it treats the restricted mana as spendable and `ActivateAbilityHandler`'s
                // authoritative payment is the only thing that rejects it. That divergence is
                // pre-existing and general to `ManaRestriction.CardTypeSpellsOrAbilitiesOnly`
                // (Castle Doom, Mishra's Workshop, …), not something this card introduces — fixing
                // it is engine work in its own right.
            }

            test("the mana can't cast a creature spell") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Shang-Chi, Master of Kung Fu", summoningSickness = true)
                    .withCardInHand(1, "Kung Fu Training Dummy")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val shangChi = game.findPermanent("Shang-Chi, Master of Kung Fu")!!
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = shangChi,
                        abilityId = shangChiManaAbilityId,
                        manaColorChoice = Color.GREEN,
                    )
                ).error shouldBe null

                withClue("the oracle allows abilities only — allowSpells = false") {
                    game.castSpell(1, "Kung Fu Training Dummy").error shouldNotBe null
                }
                restrictedMana(game).size shouldBe 2
            }
        }
    }
}
