package com.wingedsheep.ai.engine

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.legalactions.LegalAction
import com.wingedsheep.engine.legalactions.TargetInfo
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Unit tests for [XCostSelection] — which X values the AI considers, and the target narrowing that
 * keeps the resulting action legal.
 *
 * Kept off the simulation path on purpose. The end-to-end proof that the Strategist *uses* this
 * lives in [XCostSpellAiTest]; what matters here is that the candidate set and the narrowing are
 * exactly right, which a test whose answer depends on the evaluator could never pin down.
 */
class XCostSelectionTest : ScenarioTestBase() {

    /** A board with a known mana-value / power spread to derive X from. */
    private class Board(val state: GameState, val thopter: EntityId, val bears: EntityId, val giant: EntityId)

    private fun board(): Board {
        val game = scenario()
            .withPlayers()
            // Ornithopter: mana value 0, power 0. Grizzly Bears: 2 and 2. Hill Giant: 4 and 3.
            .withCardOnBattlefield(2, "Ornithopter")
            .withCardOnBattlefield(2, "Grizzly Bears")
            .withCardOnBattlefield(2, "Hill Giant")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()
        return Board(
            game.state,
            game.findPermanent("Ornithopter")!!,
            game.findPermanent("Grizzly Bears")!!,
            game.findPermanent("Hill Giant")!!,
        )
    }

    private fun castAction(
        maxAffordableX: Int?,
        minX: Int = 0,
        validTargets: List<EntityId>? = null,
        requiresTargets: Boolean = false,
        targetCount: Int = 1,
        minTargets: Int = 1,
        xConstrainsTargetManaValue: Boolean = false,
        xConstrainsTargetManaValueExactly: Boolean = false,
        xConstrainsTargetPower: Boolean = false,
        xConstrainsTargetCount: Boolean = false,
        targetRequirements: List<TargetInfo>? = null,
    ) = LegalAction(
        action = CastSpell(playerId = EntityId.generate(), cardId = EntityId.generate()),
        actionType = "CastSpell",
        description = "Cast an X spell",
        hasXCost = true,
        maxAffordableX = maxAffordableX,
        minX = minX,
        validTargets = validTargets,
        requiresTargets = requiresTargets,
        targetCount = targetCount,
        minTargets = minTargets,
        xConstrainsTargetManaValue = xConstrainsTargetManaValue,
        xConstrainsTargetManaValueExactly = xConstrainsTargetManaValueExactly,
        xConstrainsTargetPower = xConstrainsTargetPower,
        xConstrainsTargetCount = xConstrainsTargetCount,
        targetRequirements = targetRequirements,
    )

    init {
        context("a free X — nothing about the targets depends on it") {

            test("keeps the top MAX_X_CANDIDATES affordable values, biggest first") {
                val b = board()
                XCostSelection.candidateXValues(b.state, castAction(maxAffordableX = 9)) shouldBe
                    listOf(9, 8, 7, 6, 5)
            }

            test("a range shorter than the cap is offered whole") {
                val b = board()
                XCostSelection.candidateXValues(b.state, castAction(maxAffordableX = 3)) shouldBe
                    listOf(3, 2, 1)
            }

            test("X=0 is not a candidate — it is the enumerator's default and buys nothing") {
                val b = board()
                XCostSelection.candidateXValues(b.state, castAction(maxAffordableX = 1)) shouldBe listOf(1)
                XCostSelection.candidateXValues(b.state, castAction(maxAffordableX = 0)) shouldBe emptyList()
            }

            test("an 'X can't be 0' floor is respected") {
                val b = board()
                XCostSelection.candidateXValues(b.state, castAction(maxAffordableX = 4, minX = 3)) shouldBe
                    listOf(4, 3)
                // Can't afford even the minimum — no legal X at all.
                XCostSelection.candidateXValues(b.state, castAction(maxAffordableX = 2, minX = 3)) shouldBe
                    emptyList()
            }

            test("narrowing leaves an unconstrained target list untouched") {
                val b = board()
                val action = castAction(
                    maxAffordableX = 5,
                    validTargets = listOf(b.thopter, b.bears, b.giant),
                    requiresTargets = true,
                )
                val narrowed = XCostSelection.narrowToX(b.state, action, 2).shouldNotBeNull()
                narrowed.validTargets shouldBe listOf(b.thopter, b.bears, b.giant)
            }
        }

        context("X gates which targets are legal") {

            test("'mana value X or less' derives candidates from the targets' own mana values") {
                val b = board()
                val action = castAction(
                    maxAffordableX = 9,
                    validTargets = listOf(b.thopter, b.bears, b.giant),
                    requiresTargets = true,
                    xConstrainsTargetManaValue = true,
                )
                // Exactly the values that reach a permanent — never 1, 3 or 5-9, which cost more
                // mana to hit the same thing.
                XCostSelection.candidateXValues(b.state, action) shouldBe listOf(4, 2, 0)
            }

            test("candidates above what the player can pay are dropped") {
                val b = board()
                val action = castAction(
                    maxAffordableX = 3,
                    validTargets = listOf(b.thopter, b.bears, b.giant),
                    requiresTargets = true,
                    xConstrainsTargetManaValue = true,
                )
                // Hill Giant's 4 is unaffordable, so it is not offered.
                XCostSelection.candidateXValues(b.state, action) shouldBe listOf(2, 0)
            }

            test("'power X' derives candidates from projected power") {
                val b = board()
                val action = castAction(
                    maxAffordableX = 9,
                    validTargets = listOf(b.thopter, b.bears, b.giant),
                    requiresTargets = true,
                    xConstrainsTargetPower = true,
                )
                XCostSelection.candidateXValues(b.state, action) shouldBe listOf(3, 2, 0)
            }

            test("'mana value X or less' narrows the target list to what X reaches") {
                val b = board()
                val action = castAction(
                    maxAffordableX = 9,
                    validTargets = listOf(b.thopter, b.bears, b.giant),
                    requiresTargets = true,
                    xConstrainsTargetManaValue = true,
                )
                XCostSelection.narrowToX(b.state, action, 2)!!.validTargets shouldBe
                    listOf(b.thopter, b.bears)
                XCostSelection.narrowToX(b.state, action, 0)!!.validTargets shouldBe listOf(b.thopter)
            }

            test("'mana value X' exactly is an equality filter, not a ceiling") {
                val b = board()
                val action = castAction(
                    maxAffordableX = 9,
                    validTargets = listOf(b.thopter, b.bears, b.giant),
                    requiresTargets = true,
                    xConstrainsTargetManaValueExactly = true,
                )
                XCostSelection.narrowToX(b.state, action, 2)!!.validTargets shouldBe listOf(b.bears)
            }

            test("an X that reaches nothing is not a castable action") {
                val b = board()
                val action = castAction(
                    maxAffordableX = 9,
                    validTargets = listOf(b.thopter, b.bears, b.giant),
                    requiresTargets = true,
                    xConstrainsTargetManaValueExactly = true,
                )
                // No permanent has mana value 1, so this X cannot be cast.
                XCostSelection.narrowToX(b.state, action, 1).shouldBeNull()
            }
        }

        context("X caps how many targets may be chosen") {

            test("the chosen X replaces the enumerator's placeholder count") {
                val b = board()
                val action = castAction(
                    maxAffordableX = 5,
                    validTargets = listOf(b.thopter, b.bears, b.giant),
                    targetCount = 1,
                    minTargets = 0,
                    xConstrainsTargetCount = true,
                )
                XCostSelection.narrowToX(b.state, action, 3)!!.targetCount shouldBe 3
            }

            test("a count cap does not gate legality, so X is swept rather than target-derived") {
                val b = board()
                val action = castAction(
                    maxAffordableX = 4,
                    validTargets = listOf(b.thopter, b.bears, b.giant),
                    targetCount = 1,
                    minTargets = 0,
                    xConstrainsTargetCount = true,
                )
                XCostSelection.candidateXValues(b.state, action) shouldBe listOf(4, 3, 2, 1)
            }
        }

        context("multi-requirement actions") {

            test("each requirement is narrowed on its own flags") {
                val b = board()
                val action = castAction(
                    maxAffordableX = 9,
                    targetRequirements = listOf(
                        TargetInfo(
                            index = 0,
                            description = "target permanent with mana value X or less",
                            minTargets = 1,
                            maxTargets = 1,
                            validTargets = listOf(b.thopter, b.bears, b.giant),
                            xConstrainsManaValue = true,
                        ),
                        TargetInfo(
                            index = 1,
                            description = "target creature",
                            minTargets = 1,
                            maxTargets = 1,
                            validTargets = listOf(b.thopter, b.bears, b.giant),
                        ),
                    ),
                )
                val narrowed = XCostSelection.narrowToX(b.state, action, 2).shouldNotBeNull()
                narrowed.targetRequirements!![0].validTargets shouldBe listOf(b.thopter, b.bears)
                narrowed.targetRequirements!![1].validTargets shouldBe
                    listOf(b.thopter, b.bears, b.giant)
            }

            test("emptying a mandatory requirement makes the whole X illegal") {
                val b = board()
                val action = castAction(
                    maxAffordableX = 9,
                    targetRequirements = listOf(
                        TargetInfo(
                            index = 0,
                            description = "target permanent with mana value X",
                            minTargets = 1,
                            maxTargets = 1,
                            validTargets = listOf(b.bears),
                            xConstrainsManaValueExactly = true,
                        ),
                    ),
                )
                XCostSelection.narrowToX(b.state, action, 2).shouldNotBeNull()
                XCostSelection.narrowToX(b.state, action, 3).shouldBeNull()
            }

            test("an optional requirement emptied by X is not fatal") {
                val b = board()
                val action = castAction(
                    maxAffordableX = 9,
                    targetRequirements = listOf(
                        TargetInfo(
                            index = 0,
                            description = "up to one target permanent with mana value X or less",
                            minTargets = 0,
                            maxTargets = 1,
                            validTargets = listOf(b.giant),
                            xConstrainsManaValue = true,
                        ),
                    ),
                )
                val narrowed = XCostSelection.narrowToX(b.state, action, 1).shouldNotBeNull()
                narrowed.targetRequirements!![0].validTargets shouldBe emptyList()
            }
        }

        test("an action with no affordable X is not expanded at all") {
            val b = board()
            XCostSelection.candidateXValues(b.state, castAction(maxAffordableX = null)) shouldBe emptyList()
        }
    }
}
