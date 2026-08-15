package com.wingedsheep.gameserver.controller

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.gameserver.ai.AiGameManager
import com.wingedsheep.gameserver.scenario.AssayCardService
import com.wingedsheep.gameserver.scenario.AssayCompileRequest
import com.wingedsheep.gameserver.scenario.AssayCompileResponse
import com.wingedsheep.gameserver.scenario.PlayerInfo
import com.wingedsheep.gameserver.scenario.ScenarioBuilderService
import com.wingedsheep.gameserver.scenario.ScenarioRequest
import com.wingedsheep.gameserver.scenario.ScenarioResponse
import com.wingedsheep.gameserver.scenario.ScenarioSessionFactory
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val logger = LoggerFactory.getLogger(DevScenarioController::class.java)

/**
 * Development-only REST controller for creating test scenarios.
 *
 * This allows manual testing of the UI against specific game states
 * without needing to play through an entire game to reach that state.
 *
 * **WARNING:** This endpoint should NEVER be enabled in production.
 * Enable with: game.dev-endpoints.enabled=true
 */
@RestController
@RequestMapping("/api/dev/scenarios")
@ConditionalOnProperty(name = ["game.dev-endpoints.enabled"], havingValue = "true")
@Tag(name = "Dev Scenarios", description = "Development-only endpoints for creating test game scenarios")
class DevScenarioController(
    private val cardRegistry: CardRegistry,
    private val aiGameManager: AiGameManager,
    private val scenarioBuilderService: ScenarioBuilderService,
    private val scenarioSessionFactory: ScenarioSessionFactory,
    private val assayCardService: AssayCardService,
) {

    /**
     * Create a new game session with a pre-configured scenario.
     *
     * POST /api/dev/scenarios
     *
     * After creating the scenario, connect to the WebSocket at /game
     * and send a Connect message with the token returned in the response.
     */
    @PostMapping
    @Operation(
        summary = "Create a test scenario",
        description = """
            Creates a new game session with a pre-configured board state.

            After creating the scenario:
            1. Copy the token for the player you want to play as
            2. Open the web client
            3. Connect via WebSocket using the token

            The game will be in the specified phase with all cards already in place.
        """,
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = [Content(
                mediaType = "application/json",
                examples = [
                    ExampleObject(
                        name = "Butcher Orgg - Divide Combat Damage",
                        summary = "Divide 6 combat damage among defender and creatures",
                        value = """
{
  "player1Name": "Alice",
  "player2Name": "Bob",
  "player1": {
    "lifeTotal": 20,
    "hand": [],
    "battlefield": [
      {"name": "Mountain"},
      {"name": "Mountain"},
      {"name": "Mountain"},
      {"name": "Butcher Orgg"}
    ],
    "library": ["Mountain", "Mountain"]
  },
  "player2": {
    "lifeTotal": 20,
    "hand": [],
    "battlefield": [
      {"name": "Swamp"},
      {"name": "Swamp"},
      {"name": "Grizzly Bears"},
      {"name": "Hill Giant"},
      {"name": "Goblin Bully"}
    ],
    "library": ["Swamp", "Swamp"]
  },
  "phase": "PRECOMBAT_MAIN",
  "activePlayer": 1
}
                        """
                    ),
                    ExampleObject(
                        name = "Combat Tricks - Giant Growth & Terror",
                        summary = "Giant Growth pump vs Terror removal",
                        value = """
{
  "player1Name": "Alice",
  "player2Name": "Bob",
  "player1": {
    "lifeTotal": 20,
    "hand": ["Giant Growth", "Lightning Bolt"],
    "battlefield": [
      {"name": "Forest"},
      {"name": "Mountain"},
      {"name": "Grizzly Bears"}
    ],
    "library": ["Island", "Plains", "Swamp"]
  },
  "player2": {
    "lifeTotal": 18,
    "hand": ["Terror"],
    "battlefield": [
      {"name": "Swamp"},
      {"name": "Swamp"}
    ]
  },
  "phase": "PRECOMBAT_MAIN",
  "activePlayer": 1
}
                        """
                    ),
                    ExampleObject(
                        name = "Forked Lightning - Divided Damage",
                        summary = "Test damage division among 1-3 creatures",
                        value = """
{
  "player1Name": "Alice",
  "player2Name": "Bob",
  "player1": {
    "lifeTotal": 20,
    "hand": ["Forked Lightning"],
    "battlefield": [
      {"name": "Mountain"},
      {"name": "Mountain"},
      {"name": "Mountain"},
      {"name": "Mountain"}
    ],
    "library": ["Mountain", "Mountain"]
  },
  "player2": {
    "lifeTotal": 20,
    "hand": [],
    "battlefield": [
      {"name": "Raging Goblin"},
      {"name": "Grizzly Bears"},
      {"name": "Hill Giant"}
    ],
    "library": ["Swamp", "Swamp"]
  },
  "phase": "PRECOMBAT_MAIN",
  "activePlayer": 1
}
                        """
                    ),
                    ExampleObject(
                        name = "Wrath of God - Board Wipe Dilemma",
                        summary = "Clear the board or attack with your army?",
                        value = """
{
  "player1Name": "Alice",
  "player2Name": "Bob",
  "player1": {
    "lifeTotal": 12,
    "hand": ["Wrath of God"],
    "battlefield": [
      {"name": "Plains"},
      {"name": "Plains"},
      {"name": "Plains"},
      {"name": "Plains"},
      {"name": "Grizzly Bears"},
      {"name": "Devoted Hero"}
    ],
    "library": ["Plains", "Plains"]
  },
  "player2": {
    "lifeTotal": 20,
    "battlefield": [
      {"name": "Swamp"},
      {"name": "Swamp"},
      {"name": "Swamp"},
      {"name": "Arrogant Vampire"},
      {"name": "Dread Reaper"},
      {"name": "Feral Shadow"}
    ],
    "library": ["Swamp", "Swamp"]
  },
  "phase": "PRECOMBAT_MAIN",
  "activePlayer": 1
}
                        """
                    ),
                    ExampleObject(
                        name = "Astral Slide - Cycling Synergy",
                        summary = "Cycle cards to exile and return creatures",
                        value = """
{
  "player1Name": "Alice",
  "player2Name": "Bob",
  "player1": {
    "lifeTotal": 20,
    "hand": ["Barkhide Mauler", "Daru Lancer", "Aura Extraction"],
    "battlefield": [
      {"name": "Plains"},
      {"name": "Plains"},
      {"name": "Plains"},
      {"name": "Forest"},
      {"name": "Forest"},
      {"name": "Astral Slide"},
      {"name": "Grizzly Bears"}
    ],
    "library": ["Plains", "Forest", "Plains"]
  },
  "player2": {
    "lifeTotal": 20,
    "battlefield": [
      {"name": "Mountain"},
      {"name": "Mountain"},
      {"name": "Hill Giant"},
      {"name": "Raging Goblin"}
    ],
    "library": ["Mountain", "Mountain"]
  },
  "phase": "PRECOMBAT_MAIN",
  "activePlayer": 1
}
                        """
                    ),
                    ExampleObject(
                        name = "Gravedigger - Recursion",
                        summary = "Recover creatures from the graveyard",
                        value = """
{
  "player1Name": "Alice",
  "player2Name": "Bob",
  "player1": {
    "lifeTotal": 14,
    "hand": ["Gravedigger", "Raise Dead"],
    "battlefield": [
      {"name": "Swamp"},
      {"name": "Swamp"},
      {"name": "Swamp"},
      {"name": "Swamp"},
      {"name": "Bog Wraith"}
    ],
    "graveyard": ["Arrogant Vampire", "Feral Shadow", "Dread Reaper"],
    "library": ["Swamp", "Swamp"]
  },
  "player2": {
    "lifeTotal": 20,
    "battlefield": [
      {"name": "Plains"},
      {"name": "Plains"},
      {"name": "Plains"},
      {"name": "Ardent Militia"},
      {"name": "Wall of Swords"}
    ],
    "library": ["Plains", "Plains"]
  },
  "phase": "PRECOMBAT_MAIN",
  "activePlayer": 1
}
                        """
                    ),
                    ExampleObject(
                        name = "Tribal Elves - Wellwisher & Symbiotic Elf",
                        summary = "Elf tribal lifegain and token generation vs Infest",
                        value = """
{
  "player1Name": "Alice",
  "player2Name": "Bob",
  "player1": {
    "lifeTotal": 20,
    "hand": ["Symbiotic Elf", "Elvish Vanguard"],
    "battlefield": [
      {"name": "Forest"},
      {"name": "Forest"},
      {"name": "Forest"},
      {"name": "Forest"},
      {"name": "Wellwisher"},
      {"name": "Wirewood Elf"},
      {"name": "Elvish Warrior"}
    ],
    "library": ["Forest", "Forest", "Forest"]
  },
  "player2": {
    "lifeTotal": 20,
    "hand": ["Infest"],
    "battlefield": [
      {"name": "Swamp"},
      {"name": "Swamp"},
      {"name": "Swamp"},
      {"name": "Severed Legion"},
      {"name": "Nantuko Husk"}
    ],
    "library": ["Swamp", "Swamp"]
  },
  "phase": "PRECOMBAT_MAIN",
  "activePlayer": 1
}
                        """
                    ),
                    ExampleObject(
                        name = "Arcanis the Omnipotent - Card Advantage Engine",
                        summary = "Tap to draw 3 or bounce to dodge removal",
                        value = """
{
  "player1Name": "Alice",
  "player2Name": "Bob",
  "player1": {
    "lifeTotal": 20,
    "hand": ["Mystic Denial"],
    "battlefield": [
      {"name": "Island"},
      {"name": "Island"},
      {"name": "Island"},
      {"name": "Island"},
      {"name": "Island"},
      {"name": "Island"},
      {"name": "Arcanis the Omnipotent"},
      {"name": "Phantom Warrior"}
    ],
    "library": ["Island", "Wind Drake", "Cloud Spirit", "Man-o'-War", "Time Ebb", "Island"]
  },
  "player2": {
    "lifeTotal": 15,
    "hand": ["Hand of Death", "Volcanic Hammer"],
    "battlefield": [
      {"name": "Swamp"},
      {"name": "Swamp"},
      {"name": "Swamp"},
      {"name": "Mountain"},
      {"name": "Mountain"},
      {"name": "Hulking Cyclops"},
      {"name": "Raging Minotaur"}
    ],
    "library": ["Swamp", "Mountain"]
  },
  "phase": "PRECOMBAT_MAIN",
  "activePlayer": 1
}
                        """
                    ),
                    ExampleObject(
                        name = "Minimal scenario",
                        summary = "Empty board, just lands",
                        value = """
{
  "player1": {
    "battlefield": [{"name": "Forest"}, {"name": "Forest"}]
  },
  "player2": {
    "battlefield": [{"name": "Swamp"}]
  }
}
                        """
                    )
                ]
            )]
        ),
        responses = [
            ApiResponse(responseCode = "200", description = "Scenario created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid scenario configuration (e.g., unknown card name)")
        ]
    )
    fun createScenario(
        @RequestBody request: ScenarioRequest,
        @RequestParam(required = false) player1Token: String?,
        @RequestParam(required = false) player2Token: String?
    ): ResponseEntity<ScenarioResponse> {
        logger.info("Creating dev scenario: player1=${request.player1Name}, player2=${request.player2Name}, mode=${request.effectiveMode}, aiPlayer=${request.aiPlayer}")

        // AI-seat preconditions (server-config concerns, not request validity).
        if (request.aiPlayer != null && request.aiPlayer !in setOf(1, 2)) {
            return badRequest("aiPlayer must be 1 or 2 (got ${request.aiPlayer})")
        }
        if (request.effectiveMode == com.wingedsheep.gameserver.scenario.ScenarioMode.AI && !aiGameManager.aiEnabledToggle) {
            return badRequest("AI is not enabled on this server. Set game.ai.enabled=true to play scenarios against the AI.")
        }

        // Custom cards first: they define names the zone validation below has to know about.
        val custom = assayCardService.resolve(request)

        // Dev workflow is permissive: validate for clean error messages but don't enforce caps.
        val errors = custom.errors +
            scenarioBuilderService.validate(request, enforceLimits = false, registry = custom.registry)
        if (errors.isNotEmpty()) {
            return badRequest(errors.joinToString("; "))
        }

        return try {
            val build = scenarioBuilderService.buildScenario(request, registry = custom.registry)
            // Stable "p1"/"p2" tokens keep the bookmark-a-browser-tab dev workflow.
            val response = scenarioSessionFactory.createSession(
                build = build,
                request = request,
                player1Token = player1Token ?: "p1",
                player2Token = player2Token ?: "p2",
                includeDevUrls = true,
            )
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            logger.error("Failed to create scenario", e)
            badRequest("Failed to create scenario: ${e.message}")
        }
    }

    private fun badRequest(message: String): ResponseEntity<ScenarioResponse> =
        ResponseEntity.badRequest().body(
            ScenarioResponse(
                sessionId = "",
                player1 = PlayerInfo("", "", ""),
                player2 = PlayerInfo("", "", ""),
                message = message,
            )
        )

    /**
     * Compile one pasted Scryfall(-style) card object with Argentum Assay, without starting a game.
     *
     * This is the Scenario Builder's "check this card" button, and the answer it gives is the one
     * the gates give: each printed line with the touchstone's own verdict, the canonical spelling
     * where the author wrote a legal variant, and — for a line no rule reads — `assay explain`'s
     * caret on the token the parse died on. A card compiles only when Assay reads **every** line;
     * see [com.wingedsheep.gameserver.scenario.AssayCardService].
     *
     * Always 200: a card that will not compile is this endpoint's *product*, not an error.
     */
    @PostMapping("/assay")
    @Operation(
        summary = "Compile a custom card with Argentum Assay",
        description = "Reads a Scryfall(-style) card object into a real CardDefinition, or reports " +
            "exactly which printed line stopped it. Also the check for cards that have no Scryfall " +
            "entry at all — the JSON is just the card's printed characteristics.",
    )
    fun assayCard(@RequestBody request: AssayCompileRequest): ResponseEntity<AssayCompileResponse> =
        ResponseEntity.ok(assayCardService.inspect(request.json))

    /**
     * List available cards for scenario building.
     */
    @GetMapping("/cards")
    @Operation(
        summary = "List available cards",
        description = "Returns a sorted list of all card names that can be used in scenarios."
    )
    fun listCards(): ResponseEntity<List<String>> {
        val cardNames = cardRegistry.allCardNames().sorted()
        return ResponseEntity.ok(cardNames)
    }
}
