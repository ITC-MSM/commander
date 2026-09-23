package com.wingedsheep.engine.scenarios
import com.wingedsheep.engine.state.components.battlefield.ChoiceValue
import com.wingedsheep.sdk.scripting.ChoiceSlot
import com.wingedsheep.engine.state.components.battlefield.CastChoicesComponent

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.legalactions.EnumerationMode
import com.wingedsheep.engine.legalactions.LegalActionEnumerator
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AlternativePaymentChoice
import com.wingedsheep.sdk.scripting.ConvokePayment
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.wingedsheep.engine.core.Outcome

/**
 * Reproduces a user-reported flow: casting Sun-Dappled Celebrant ({4}{W}{W}, Convoke)
 * when one of the player's Forests is enchanted by Shimmerwilds Growth.
 *
 * Board state:
 *   Hand:        Sun-Dappled Celebrant
 *   Battlefield: Plains, Forest×4 (one enchanted by Shimmerwilds Growth, chosen GREEN),
 *                second Sun-Dappled Celebrant for convoke
 *
 * Mana math (intentionally tight): 1 Plains + 4 Forests with the enchanted Forest
 * tapping for {G}{G} produces 5G + 1W = 6 mana but only **one** W. The cost {4}{W}{W}
 * needs two Ws — one from the Plains, one from convoking the second Celebrant. So
 * convoke is required, and only the colored {W} payment satisfies the math; convoking
 * for generic {1} would leave the spell one W short.
 */
class SunDappledCelebrantConvokeTest : FunSpec({

    /**
     * Manually attach Shimmerwilds Growth to a target land with a chosen color.
     * Mirrors the runtime aura state (CardComponent + CastChoicesComponent +
     * AttachedToComponent), which is what the mana solver's
     * `augmentWithAuraBonusMana` and the override-color lookup read at solve time.
     */
    fun GameTestDriver.attachShimmerwildsGrowth(
        playerId: EntityId,
        targetLandId: EntityId,
        chosenColor: Color
    ): EntityId {
        val cardDef = cardRegistry.requireCard("Shimmerwilds Growth")
        val auraId = EntityId.generate()

        val cardComponent = CardComponent(
            cardDefinitionId = cardDef.name,
            name = cardDef.name,
            manaCost = cardDef.manaCost,
            typeLine = cardDef.typeLine,
            oracleText = cardDef.oracleText,
            baseStats = cardDef.creatureStats,
            baseKeywords = cardDef.keywords,
            baseFlags = cardDef.flags,
            colors = cardDef.colors,
            ownerId = playerId,
            spellEffect = cardDef.spellEffect
        )

        val container = ComponentContainer.of(
            cardComponent,
            OwnerComponent(playerId),
            ControllerComponent(playerId),
            AttachedToComponent(targetLandId),
            CastChoicesComponent(chosen = mapOf(ChoiceSlot.COLOR to ChoiceValue.ColorChoice(chosenColor)))
        )

        var newState = state.withEntity(auraId, container)
        newState = newState.addToZone(ZoneKey(playerId, Zone.BATTLEFIELD), auraId)

        val existingAttachments = newState.getEntity(targetLandId)
            ?.get<AttachmentsComponent>()?.attachedIds ?: emptyList()
        newState = newState.updateEntity(targetLandId) { c ->
            c.with(AttachmentsComponent(existingAttachments + auraId))
        }

        replaceState(newState)
        return auraId
    }

    /** Returns (caster, lands, convokeCreature). Lands order: Plains, F-enchanted, F, F, F. */
    fun setupBoard(driver: GameTestDriver): Triple<EntityId, List<EntityId>, EntityId> {
        val caster = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // 1 Plains + 4 Forests; the first Forest is enchanted by Shimmerwilds Growth
        // with chosen color GREEN, so it taps for {G}{G} (override is a no-op + bonus G).
        val plains = driver.putLandOnBattlefield(caster, "Plains")
        val forestEnchanted = driver.putLandOnBattlefield(caster, "Forest")
        val forest2 = driver.putLandOnBattlefield(caster, "Forest")
        val forest3 = driver.putLandOnBattlefield(caster, "Forest")
        val forest4 = driver.putLandOnBattlefield(caster, "Forest")
        val lands = listOf(plains, forestEnchanted, forest2, forest3, forest4)

        driver.attachShimmerwildsGrowth(caster, forestEnchanted, Color.GREEN)

        // Second Sun-Dappled Celebrant on the battlefield; clear summoning sickness so
        // it is a legal convoke source.
        val convokeCreature = driver.putCreatureOnBattlefield(caster, "Sun-Dappled Celebrant")
        driver.removeSummoningSickness(convokeCreature)

        return Triple(caster, lands, convokeCreature)
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(
            deck = Deck.of("Forest" to 20, "Plains" to 20),
            startingLife = 20,
            skipMulligans = true
        )
        return driver
    }

    test("auto-pay + convoke (white creature paying {W}) can cast Sun-Dappled Celebrant") {
        // Convoke the second Sun-Dappled Celebrant for {W}; remaining {4}{W} should
        // be coverable by the 5 lands once the Shimmerwilds bonus G is counted.
        val driver = createDriver()
        val (caster, _, convokeCreature) = setupBoard(driver)

        val spellId = driver.putCardInHand(caster, "Sun-Dappled Celebrant")

        val result = driver.submit(
            CastSpell(
                playerId = caster,
                cardId = spellId,
                paymentStrategy = PaymentStrategy.AutoPay,
                alternativePayment = AlternativePaymentChoice(
                    convokedCreatures = mapOf(convokeCreature to ConvokePayment(color = Color.WHITE))
                )
            )
        )

        result.outcome shouldBe Outcome.Done
    }

    test("LegalActionEnumerator surfaces Sun-Dappled Celebrant as castable") {
        // What the UI sees: enumerate the player's legal actions and verify a CastSpell
        // for Sun-Dappled Celebrant is among them.
        val driver = createDriver()
        val (caster, _, _) = setupBoard(driver)
        val spellId = driver.putCardInHand(caster, "Sun-Dappled Celebrant")

        val enumerator = LegalActionEnumerator.create(driver.cardRegistry)
        val legalActions = enumerator.enumerate(driver.state, caster, EnumerationMode.FULL)

        val castAction = legalActions.firstOrNull { la ->
            (la.action as? CastSpell)?.cardId == spellId
        }
        (castAction != null) shouldBe true
    }

    test("Explicit + convoke after partial float: validator must subtract pool before checking chosen sources") {
        // Repro for the FE error "Selected mana sources cannot pay this spell's cost".
        // User flow: tap one Forest and the Plains for mana before clicking cast, then
        // convoke the second Celebrant for {W} and pick the remaining lands in the
        // mana-source phase. The validator used to demand the chosen sources alone
        // cover the full {4}{W}{W} (convoke-reduced to {4}{W}) — ignoring the 1G+1W
        // already floating — and incorrectly rejected the cast.
        val driver = createDriver()
        val (caster, lands, convokeCreature) = setupBoard(driver)
        val plains = lands[0]
        val forestEnchanted = lands[1]
        val forestPlain1 = lands[2]
        val forestPlain2 = lands[3]
        val forestPlain3 = lands[4]

        val forestAbilityId = driver.cardRegistry.requireCard("Forest").activatedAbilities[0].id
        val plainsAbilityId = driver.cardRegistry.requireCard("Plains").activatedAbilities[0].id

        // Float 1G + 1W by tapping a plain Forest + the Plains.
        driver.submit(ActivateAbility(caster, forestPlain1, forestAbilityId)).outcome shouldBe Outcome.Done
        driver.submit(ActivateAbility(caster, plains, plainsAbilityId)).outcome shouldBe Outcome.Done
        val pool = driver.state.getEntity(caster)!!.get<ManaPoolComponent>()!!
        pool.green shouldBe 1
        pool.white shouldBe 1

        val spellId = driver.putCardInHand(caster, "Sun-Dappled Celebrant")

        // Pay {4}{W}{W}: convoke the second Celebrant for {W}, leaving {4}{W} of which
        // {1}+{W} is in the pool. Tap the enchanted Forest (2G) + 2 plain Forests (2G)
        // for the remaining {3} generic — exactly enough.
        val result = driver.submit(
            CastSpell(
                playerId = caster,
                cardId = spellId,
                paymentStrategy = PaymentStrategy.Explicit(
                    manaAbilitiesToActivate = listOf(forestEnchanted, forestPlain2, forestPlain3)
                ),
                alternativePayment = AlternativePaymentChoice(
                    convokedCreatures = mapOf(convokeCreature to ConvokePayment(color = Color.WHITE))
                )
            )
        )
        result.outcome shouldBe Outcome.Done
    }

    test("FromPool + convoke: float lands first, then cast Sun-Dappled Celebrant") {
        // Hardcore-mode flow: player manually taps lands to float mana before casting.
        // Tapping all five lands floats 5G + 1W; convoking the second Celebrant for
        // {W} reduces the cost to {4}{W}, which the pool exactly covers (4G generic
        // + 1W colored, with one G left over).
        val driver = createDriver()
        val (caster, lands, convokeCreature) = setupBoard(driver)

        val forestAbilityId = driver.cardRegistry.requireCard("Forest").activatedAbilities[0].id
        val plainsAbilityId = driver.cardRegistry.requireCard("Plains").activatedAbilities[0].id

        for (landId in lands) {
            val cardName = driver.state.getEntity(landId)
                ?.get<CardComponent>()?.name
            val abilityId = if (cardName == "Plains") plainsAbilityId else forestAbilityId
            driver.submit(ActivateAbility(caster, landId, abilityId)).outcome shouldBe Outcome.Done
        }

        // Pool: enchanted Forest gave 2G (override no-op + Shimmerwilds bonus G),
        // 3 other Forests gave 3G, the Plains gave 1W → 5G + 1W.
        val pool = driver.state.getEntity(caster)!!.get<ManaPoolComponent>()!!
        pool.green shouldBe 5
        pool.white shouldBe 1

        val spellId = driver.putCardInHand(caster, "Sun-Dappled Celebrant")

        val result = driver.submit(
            CastSpell(
                playerId = caster,
                cardId = spellId,
                paymentStrategy = PaymentStrategy.FromPool,
                alternativePayment = AlternativePaymentChoice(
                    convokedCreatures = mapOf(convokeCreature to ConvokePayment(color = Color.WHITE))
                )
            )
        )
        result.outcome shouldBe Outcome.Done

        // Convoke covered one {W}, pool paid {4}{W}: 4 generic from 4G, 1W from
        // the Plains. One green should remain.
        val poolAfter = driver.state.getEntity(caster)!!.get<ManaPoolComponent>()!!
        poolAfter.white shouldBe 0
        poolAfter.green shouldBe 1
    }
})
