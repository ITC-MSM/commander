package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.registry.TokenArtRegistry
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.hob.TheHobbitSet
import com.wingedsheep.mtg.sets.tokens.TokenArtData
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

/**
 * The Hobbit's tokens show the token *card* the set printed.
 *
 * The client renders a token's `imageUri` as-is unless the URL says `/art_crop/`, in which case it
 * takes the legacy path and draws the bare art inside a frame it generates itself. HOB rows were
 * hand-authored as `art_crop`, so tokens with a perfectly good printed card — Treasure, the recruit
 * Soldier — came out as generated placeholders instead. These tests pin the whole set to the
 * `normal` full-card form the bulk sync writes.
 *
 * They also pin the *layering*: the cards pass no `imageUri` of their own, so the art has to come
 * from [TheHobbitSet.tokenArt] through
 * [com.wingedsheep.engine.registry.TokenArtRegistry]. That is what lets a reprint mint its own
 * set's token, and it is why baking the URL into a card script is the wrong place for it.
 */
class TheHobbitTokenArtScenarioTest : ScenarioTestBase() {

    /** `thob` #6 — The Hobbit's Dwarf token. */
    private val hobbitDwarf = "9fcb3a3f-c0d4-43d4-8549-826a38bfa27d"

    /** `thob` #12 — the first of The Hobbit's two Treasure illustrations. */
    private val hobbitTreasure = "c6e096bb-ad9e-4a8b-8b42-26852fa32c1d"

    private fun registry() = TokenArtRegistry().apply {
        register(
            TheHobbitSet.code,
            TokenArtData.forSet(TheHobbitSet),
            TheHobbitSet.cards.map { it.name },
        )
    }

    init {
        test("Dwarven Shortsword's Dwarf carries the printed Hobbit token card") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Dwarven Shortsword")
                .withLandsOnBattlefield(1, "Plains", 4)
                .withCardInLibrary(1, "Plains")
                .build()

            game.castSpell(1, "Dwarven Shortsword").error shouldBe null
            game.resolveStack()

            val dwarf = game.findPermanent("Dwarf Token")
            dwarf shouldNotBe null
            val art = game.state.getEntity(dwarf!!)?.get<CardComponent>()?.imageUri

            art shouldNotBe null
            art!! shouldContain hobbitDwarf
            // The whole card, not the art box — anything under /art_crop/ takes the client's
            // generated-frame path and renders as a placeholder.
            art shouldContain "/normal/"
            art shouldNotContain "/art_crop/"
        }

        test("a Treasure minted in The Hobbit shows the Hobbit Treasure card") {
            // The predefined-token path (Effects.CreateTreasure) consults the same registry by token
            // name, so this is where the set's rows have to reach a token that has no CreateToken
            // effect to hang an override on.
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Long-Bodied Grey Dog")
                .withLandsOnBattlefield(1, "Plains", 3)
                .withCardInLibrary(1, "Plains")
                .build()

            game.castSpell(1, "Long-Bodied Grey Dog").error shouldBe null
            game.resolveStack()

            val treasure = game.findPermanent("Treasure")
            treasure shouldNotBe null
            val art = game.state.getEntity(treasure!!)?.get<CardComponent>()?.imageUri

            art shouldNotBe null
            art!! shouldContain hobbitTreasure
            art shouldContain "/normal/"
            art shouldNotContain "/art_crop/"
        }

        test("every Hobbit token row is a full-card image") {
            // The regression this file exists for: one art_crop URL is enough to send a token back
            // to the generated frame, and it looks fine in every test that only checks *which*
            // printing the token resolved to.
            TheHobbitSet.tokenArt.forEach { printing ->
                withClue(printing.name) {
                    printing.imageUri shouldContain "/normal/"
                    printing.imageUri shouldNotContain "/art_crop/"
                }
            }
        }

        test("the set covers the tokens its cards actually mint") {
            // Named and predefined tokens resolve by name alone, so they have no creature type for
            // the engine-wide fallback to key on — without a row they would reach the client with
            // whatever art the shared PredefinedTokens definition carries, from some other set.
            val registry = registry()

            for (token in listOf("Human Soldier", "Bird Soldier", "Dwarf", "Bear", "Elf", "Wolf", "Dragon")) {
                withClue(token) {
                    registry.resolve(sourceCardDefinitionId = "Dwarven Shortsword", tokenName = token)
                        .shouldNotBeNull()
                }
            }
            registry.resolve(sourceCardDefinitionId = "Iron Hills Blacksmith", tokenName = "Axe")
                .shouldNotBeNull()
            registry.resolve(sourceCardDefinitionId = "Stone-Giant of High Pass", tokenName = "Stone Boulder")
                .shouldNotBeNull()
        }

        test("the two printed Goblin Army and Treasure illustrations are both declared") {
            // Amass puts every Army counter on one token, but the Treasure makers can mint a batch,
            // and a set that printed a token twice should deal out both arts rather than repeat one.
            val registry = registry()

            registry.resolveAll(
                sourceCardDefinitionId = "Down, Down to Goblin-town",
                tokenName = "Goblin Army",
            ) shouldHaveSize 2
            registry.resolveAll(
                sourceCardDefinitionId = "Long-Bodied Grey Dog",
                tokenName = "Treasure",
            ) shouldHaveSize 2
        }
    }
}
