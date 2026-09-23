package com.wingedsheep.engine.handlers

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ktk.cards.DisownedAncestor
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import com.wingedsheep.engine.core.Outcome
import io.kotest.matchers.shouldNotBe

/**
 * BDD specification for "activate only as a sorcery" timing enforcement.
 *
 * CR 602.5d: Activated abilities that read "Activate only as a sorcery" mean the
 * player must follow the timing rules for casting a sorcery spell. CR 307.5
 * defines that timing: the player must have priority, it must be the main phase
 * of their turn, and the stack must be empty.
 *
 * Uses Disowned Ancestor's Outlast ability ({1}{B}, {T}: put a +1/+1 counter on
 * this creature. Outlast only as a sorcery.) as the specimen.
 */
class ActivateOnlyAsASorceryTest : FunSpec({

    val outlastAbilityId = DisownedAncestor.activatedAbilities.first().id

    fun setup(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(DisownedAncestor))
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 40),
            skipMulligans = true
        )
        return driver
    }

    context("Sorcery-speed activated ability — timing restriction") {

        /**
         * GIVEN a creature with "activate only as a sorcery" on the battlefield
         * AND the active player holds priority during the declare-attackers step
         * WHEN the active player attempts to activate the ability
         * THEN the engine rejects the activation
         * AND the game state and stack are unchanged
         */
        test("WHEN priority is held during declare-attackers step THEN activation is rejected and stack is unchanged") {
            val driver = setup()
            val p1 = driver.activePlayer!!

            driver.putCreatureOnBattlefield(p1, "Disowned Ancestor").also { ancestor ->
                driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

                val stackSizeBefore = driver.state.stack.size

                val result = driver.submit(
                    ActivateAbility(
                        playerId = p1,
                        sourceId = ancestor,
                        abilityId = outlastAbilityId
                    )
                )

                result.outcome shouldNotBe Outcome.Done
                result.error.shouldNotBeNull() shouldContain "sorcery"
                driver.state.stack.size shouldBe stackSizeBefore
            }
        }

        /**
         * GIVEN the same creature and ability
         * AND the active player holds priority during their main phase with an empty stack
         * WHEN the active player activates the ability
         * THEN the engine accepts the activation
         */
        test("WHEN priority is held during main phase with empty stack THEN activation is accepted") {
            val driver = setup()
            val p1 = driver.activePlayer!!

            val ancestor = driver.putCreatureOnBattlefield(p1, "Disowned Ancestor")
            driver.removeSummoningSickness(ancestor)

            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
            driver.giveMana(p1, Color.BLACK, 3)

            val result = driver.submit(
                ActivateAbility(
                    playerId = p1,
                    sourceId = ancestor,
                    abilityId = outlastAbilityId
                )
            )

            result.outcome shouldBe Outcome.Done
        }
    }
})
