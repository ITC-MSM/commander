package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.components.player.RestrictedManaEntry
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.*
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddColorlessManaEffect
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import com.wingedsheep.engine.core.Outcome

/**
 * Tests for conditional mana (mana with spending restrictions).
 *
 * Verifies that:
 * - Restricted mana is stored separately in the mana pool
 * - Restricted mana CAN pay for eligible spells
 * - Restricted mana CANNOT pay for ineligible spells
 * - AutoPay solver excludes restricted sources for ineligible spells
 */
class ConditionalManaTest : FunSpec({

    // Test card: creature with InstantOrSorceryOnly restricted mana ability
    val restrictedArcanist = card("Restricted Arcanist") {
        manaCost = "{1}{U}"
        typeLine = "Creature — Merfolk Wizard"
        power = 1
        toughness = 3

        activatedAbility {
            cost = AbilityCost.Tap
            effect = AddColorlessManaEffect(1, ManaRestriction.InstantOrSorceryOnly)
            manaAbility = true
            timing = TimingRule.ManaAbility
        }
    }

    // Test card: creature with unrestricted + restricted abilities (like Elfhame Druid)
    val dualManaCreature = card("Dual Mana Creature") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Elf Druid"
        power = 0
        toughness = 2

        activatedAbility {
            cost = AbilityCost.Tap
            effect = AddManaEffect(Color.GREEN)
            manaAbility = true
            timing = TimingRule.ManaAbility
        }

        activatedAbility {
            cost = AbilityCost.Tap
            effect = AddManaEffect(Color.GREEN, 2, ManaRestriction.KickedSpellsOnly)
            manaAbility = true
            timing = TimingRule.ManaAbility
        }
    }

    // Simple instant for testing
    val testInstant = card("Test Instant") {
        manaCost = "{1}"
        typeLine = "Instant"
        oracleText = "Draw a card."

        spell {
            effect = Effects.DrawCards(1)
        }
    }

    // Simple creature for testing
    val testCreature = card("Test Creature") {
        manaCost = "{1}"
        typeLine = "Creature — Human"
        power = 1
        toughness = 1
    }

    val arcanistAbilityId = restrictedArcanist.activatedAbilities[0].id

    fun createDriver(extraCards: List<CardDefinition> = emptyList()): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + extraCards)
        return driver
    }

    context("restricted mana pool tracking") {

        test("activating restricted mana ability adds RestrictedManaEntry to pool") {
            val driver = createDriver(listOf(restrictedArcanist))
            driver.initMirrorMatch(
                deck = Deck.of("Island" to 20),
                skipMulligans = true
            )

            val activePlayer = driver.activePlayer!!
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            val arcanist = driver.putCreatureOnBattlefield(activePlayer, "Restricted Arcanist")
            driver.removeSummoningSickness(arcanist)

            // Activate the restricted mana ability
            val activateResult = driver.submit(
                ActivateAbility(
                    playerId = activePlayer,
                    sourceId = arcanist,
                    abilityId = arcanistAbilityId
                )
            )
            activateResult.outcome shouldBe Outcome.Done

            // Check that the mana pool has restricted mana
            val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
            pool shouldNotBe null
            pool!!.restrictedMana.size shouldBe 1
            pool.restrictedMana[0].color shouldBe null // colorless
            pool.restrictedMana[0].restriction shouldBe ManaRestriction.InstantOrSorceryOnly

            // Unrestricted colorless should still be 0
            pool.colorless shouldBe 0
        }
    }

    context("restricted mana spending enforcement") {

        test("restricted mana CAN pay for eligible spell via FromPool") {
            val driver = createDriver(listOf(testInstant))
            driver.initMirrorMatch(
                deck = Deck.of("Island" to 20),
                skipMulligans = true
            )

            val activePlayer = driver.activePlayer!!
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            // Add restricted colorless mana to pool directly
            driver.giveRestrictedMana(activePlayer, null, 1, ManaRestriction.InstantOrSorceryOnly)

            // Cast an instant (eligible for InstantOrSorceryOnly)
            val instantId = driver.putCardInHand(activePlayer, "Test Instant")
            val castResult = driver.submit(
                CastSpell(
                    playerId = activePlayer,
                    cardId = instantId,
                    paymentStrategy = PaymentStrategy.FromPool
                )
            )
            castResult.outcome shouldBe Outcome.Done

            // Restricted mana should be spent
            val pool = driver.state.getEntity(activePlayer)?.get<ManaPoolComponent>()
            pool!!.restrictedMana.size shouldBe 0
        }

        test("restricted mana CANNOT pay for ineligible spell via FromPool") {
            val driver = createDriver(listOf(testCreature))
            driver.initMirrorMatch(
                deck = Deck.of("Island" to 20),
                skipMulligans = true
            )

            val activePlayer = driver.activePlayer!!
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            // Add restricted colorless mana to pool
            driver.giveRestrictedMana(activePlayer, null, 1, ManaRestriction.InstantOrSorceryOnly)

            // Try to cast a creature (not instant/sorcery) — should fail
            val creatureId = driver.putCardInHand(activePlayer, "Test Creature")
            val castResult = driver.submit(
                CastSpell(
                    playerId = activePlayer,
                    cardId = creatureId,
                    paymentStrategy = PaymentStrategy.FromPool
                )
            )
            castResult.outcome shouldNotBe Outcome.Done
        }

        test("AutoPay solver excludes restricted-only sources for ineligible spells") {
            val driver = createDriver(listOf(restrictedArcanist, testCreature))
            driver.initMirrorMatch(
                deck = Deck.of("Island" to 20),
                skipMulligans = true
            )

            val activePlayer = driver.activePlayer!!
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            // Only mana source is the restricted arcanist (no lands)
            val arcanist = driver.putCreatureOnBattlefield(activePlayer, "Restricted Arcanist")
            driver.removeSummoningSickness(arcanist)

            // Try to cast a creature — arcanist can only produce mana for instants/sorceries
            val creatureId = driver.putCardInHand(activePlayer, "Test Creature")
            val castResult = driver.submit(
                CastSpell(
                    playerId = activePlayer,
                    cardId = creatureId,
                    paymentStrategy = PaymentStrategy.AutoPay
                )
            )
            castResult.outcome shouldNotBe Outcome.Done
        }

        test("AutoPay solver USES restricted source for eligible spells") {
            val driver = createDriver(listOf(restrictedArcanist, testInstant))
            driver.initMirrorMatch(
                deck = Deck.of("Island" to 20),
                skipMulligans = true
            )

            val activePlayer = driver.activePlayer!!
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            // Only mana source is the restricted arcanist
            val arcanist = driver.putCreatureOnBattlefield(activePlayer, "Restricted Arcanist")
            driver.removeSummoningSickness(arcanist)

            // Cast an instant — arcanist can produce mana for instants
            val instantId = driver.putCardInHand(activePlayer, "Test Instant")
            val castResult = driver.submit(
                CastSpell(
                    playerId = activePlayer,
                    cardId = instantId,
                    paymentStrategy = PaymentStrategy.AutoPay
                )
            )
            castResult.outcome shouldBe Outcome.Done
        }
    }

    context("dual-ability sources") {

        test("dual mana creature is treated as unrestricted by solver (has unrestricted ability)") {
            val driver = createDriver(listOf(dualManaCreature, testCreature))
            driver.initMirrorMatch(
                deck = Deck.of("Forest" to 20),
                skipMulligans = true
            )

            val activePlayer = driver.activePlayer!!
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            val creature = driver.putCreatureOnBattlefield(activePlayer, "Dual Mana Creature")
            driver.removeSummoningSickness(creature)

            val testCard = driver.putCardInHand(activePlayer, "Test Creature")

            // Should succeed because the unrestricted {G} ability makes the source usable
            val castResult = driver.submit(
                CastSpell(
                    playerId = activePlayer,
                    cardId = testCard,
                    paymentStrategy = PaymentStrategy.AutoPay
                )
            )
            castResult.outcome shouldBe Outcome.Done
        }
    }

    context("mixed colorless-unrestricted and colored-restricted sources") {

        // Oakhollow Village pattern: unrestricted {C} + restricted {G} (creature spells only)
        val villageStyleLand = card("Village Style Land") {
            typeLine = "Land"

            activatedAbility {
                cost = AbilityCost.Tap
                effect = AddColorlessManaEffect(1)
                manaAbility = true
                timing = TimingRule.ManaAbility
            }

            activatedAbility {
                cost = AbilityCost.Tap
                effect = AddManaEffect(Color.GREEN, restriction = ManaRestriction.CreatureSpellsOnly)
                manaAbility = true
                timing = TimingRule.ManaAbility
            }
        }

        val greenSorcery = card("Green Sorcery") {
            manaCost = "{G}"
            typeLine = "Sorcery"

            spell {
                effect = Effects.DrawCards(1)
            }
        }

        val greenCreature = card("Green Test Creature") {
            manaCost = "{G}"
            typeLine = "Creature — Beast"
            power = 2
            toughness = 2
        }

        test("restricted colored mana cannot be used for ineligible spells even when source has unrestricted colorless") {
            val driver = createDriver(listOf(villageStyleLand, greenSorcery))
            driver.initMirrorMatch(
                deck = Deck.of("Forest" to 20),
                skipMulligans = true
            )

            val activePlayer = driver.activePlayer!!
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            // Only mana source is the village-style land (unrestricted {C} + restricted {G})
            driver.putPermanentOnBattlefield(activePlayer, "Village Style Land")

            val sorceryId = driver.putCardInHand(activePlayer, "Green Sorcery")

            // Should FAIL: the green ability is creature-only, and the colorless ability can't pay {G}
            val castResult = driver.submit(
                CastSpell(
                    playerId = activePlayer,
                    cardId = sorceryId,
                    paymentStrategy = PaymentStrategy.AutoPay
                )
            )
            castResult.outcome shouldNotBe Outcome.Done
        }

        test("restricted colored mana CAN be used for eligible spells from mixed source") {
            val driver = createDriver(listOf(villageStyleLand, greenCreature))
            driver.initMirrorMatch(
                deck = Deck.of("Forest" to 20),
                skipMulligans = true
            )

            val activePlayer = driver.activePlayer!!
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            driver.putPermanentOnBattlefield(activePlayer, "Village Style Land")

            val creatureId = driver.putCardInHand(activePlayer, "Green Test Creature")

            // Should SUCCEED: green creature can use the creature-only green mana
            val castResult = driver.submit(
                CastSpell(
                    playerId = activePlayer,
                    cardId = creatureId,
                    paymentStrategy = PaymentStrategy.AutoPay
                )
            )
            castResult.outcome shouldBe Outcome.Done
        }

        test("unrestricted colorless from mixed source can still pay generic costs") {
            val driver = createDriver(listOf(villageStyleLand, testCreature))
            driver.initMirrorMatch(
                deck = Deck.of("Forest" to 20),
                skipMulligans = true
            )

            val activePlayer = driver.activePlayer!!
            driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

            driver.putPermanentOnBattlefield(activePlayer, "Village Style Land")

            val creatureId = driver.putCardInHand(activePlayer, "Test Creature")

            // Should SUCCEED: {1} generic cost can be paid with unrestricted colorless mana
            val castResult = driver.submit(
                CastSpell(
                    playerId = activePlayer,
                    cardId = creatureId,
                    paymentStrategy = PaymentStrategy.AutoPay
                )
            )
            castResult.outcome shouldBe Outcome.Done
        }
    }
})
