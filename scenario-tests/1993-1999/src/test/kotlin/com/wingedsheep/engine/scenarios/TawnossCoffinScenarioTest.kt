package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Tawnos's Coffin (ATQ #68):
 *
 *   {4} Artifact — "You may choose not to untap this artifact during your untap step.
 *    {3}, {T}: Exile target creature and all Auras attached to it. Note the number and kind of
 *    counters that were on that creature. When this artifact leaves the battlefield or becomes
 *    untapped, return that exiled card to the battlefield under its owner's control tapped with the
 *    noted number and kind of counters on it. If you do, return the other exiled cards to the
 *    battlefield under their owner's control attached to that permanent."
 */
class TawnossCoffinScenarioTest : ScenarioTestBase() {

    // A simple Aura that pumps the enchanted creature, so its presence is observable.
    private val boon = card("Test Boon") {
        manaCost = "{1}{W}"
        typeLine = "Enchantment — Aura"
        oracleText = "Enchanted creature gets +1/+1."
        auraTarget = Targets.Creature
        staticAbility { ability = ModifyStats(1, 1, GroupFilter.attachedCreature()) }
    }

    // {0} sorcery that destroys a target artifact, to send Tawnos's Coffin to the graveyard.
    private val smash = card("Smash Artifact") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "Destroy target artifact."
        spell {
            val t = target("target artifact", Targets.Artifact)
            effect = Effects.Destroy(t)
        }
    }

    // {0} sorcery that untaps a target artifact, to fire the Coffin's becomes-untapped trigger.
    private val unwind = card("Unwind Artifact") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "Untap target artifact."
        spell {
            val t = target("target artifact", Targets.Artifact)
            effect = Effects.Untap(t)
        }
    }

    private val coffinAbilityId =
        cardRegistry.getCard("Tawnos's Coffin")?.activatedAbilities?.first()?.id

    init {
        cardRegistry.register(boon)
        cardRegistry.register(smash)
        cardRegistry.register(unwind)

        context("Tawnos's Coffin") {

            // Exile Grizzly Bears (carrying a +1/+1 counter and Test Boon) with the given game.
            // Returns the Coffin id.
            fun exile(game: TestGame): EntityId {
                val coffin = game.findPermanent("Tawnos's Coffin")!!
                val bear = game.findPermanent("Grizzly Bears")!!

                game.state = game.state.updateEntity(bear) {
                    it.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 1)))
                }

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = coffin,
                        abilityId = coffinAbilityId!!,
                        targets = listOf(ChosenTarget.Permanent(bear)),
                    )
                )
                result.error shouldBe null
                game.resolveStack()

                withClue("Grizzly Bears is exiled") { game.isOnBattlefield("Grizzly Bears") shouldBe false }
                withClue("Test Boon is exiled with it") { game.isOnBattlefield("Test Boon") shouldBe false }
                return coffin
            }

            fun assertReturnedWithCounterAndAura(game: TestGame) {
                val returnedBear = game.findPermanent("Grizzly Bears")!!
                withClue("Grizzly Bears returns to the battlefield tapped") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                    game.state.getEntity(returnedBear)!!.has<TappedComponent>() shouldBe true
                }
                withClue("its noted +1/+1 counter is restored") {
                    game.state.getEntity(returnedBear)!!.get<CountersComponent>()
                        ?.counters?.get(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                }
                val returnedBoon = game.findPermanent("Test Boon")!!
                withClue("the Aura returns attached to the creature") {
                    game.isOnBattlefield("Test Boon") shouldBe true
                    game.state.getEntity(returnedBoon)!!.get<AttachedToComponent>()?.targetId shouldBe returnedBear
                    game.state.getEntity(returnedBear)!!.get<AttachmentsComponent>()
                        ?.attachedIds?.contains(returnedBoon) shouldBe true
                }
            }

            test("returns the creature tapped with noted counters and re-attaches its Aura when the Coffin leaves") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Tawnos's Coffin")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Test Boon", "Grizzly Bears")
                    .withCardInHand(1, "Smash Artifact")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val coffin = exile(game)

                // Destroy the Coffin → leaves-the-battlefield trigger returns the exiled cards.
                game.castSpell(1, "Smash Artifact", coffin).error shouldBe null
                game.resolveStack()
                game.resolveStack()

                assertReturnedWithCounterAndAura(game)
            }

            test("returns the creature when the Coffin becomes untapped") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Tawnos's Coffin")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardAttachedTo(1, "Test Boon", "Grizzly Bears")
                    .withCardInHand(1, "Unwind Artifact")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val coffin = exile(game)
                withClue("the Coffin is tapped after activating its ability") {
                    game.state.getEntity(coffin)!!.has<TappedComponent>() shouldBe true
                }

                // Untap the Coffin → becomes-untapped trigger returns the exiled cards.
                game.castSpell(1, "Unwind Artifact", coffin).error shouldBe null
                game.resolveStack()
                game.resolveStack()

                withClue("the Coffin is untapped") {
                    game.state.getEntity(coffin)!!.has<TappedComponent>() shouldBe false
                }
                assertReturnedWithCounterAndAura(game)
            }
        }
    }
}
