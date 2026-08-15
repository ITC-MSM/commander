package com.wingedsheep.tooling.coverage

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.File
import java.text.Normalizer

/**
 * Shared infrastructure for the mtgish coverage tooling.
 *
 * The mtgish IR and the Scryfall payloads are deeply dynamic JSON (discriminator keys like
 * `_Action`/`_Rule` plus an `args` of any shape), so we work on a parsed [JsonElement] tree with a
 * set of small "walk the tree" accessors and recursive finders. Some rules are easiest to detect by
 * serialising a subtree and regexing the string; [compact] gives a compact, order-preserving
 * encoding for exactly that (parsing keeps key order, so the output is deterministic).
 */

/** Compact, order-preserving JSON. Parsing keeps key order (LinkedHashMap), re-encoding preserves it. */
val J: Json = Json

/** Repo root, discovered by walking up from the working dir until `settings.gradle.kts` is found. */
val REPO_ROOT: File by lazy {
    var dir: File? = File(System.getProperty("user.dir")).absoluteFile
    while (dir != null) {
        if (File(dir, "settings.gradle.kts").isFile && File(dir, "mtg-sdk").isDirectory) return@lazy dir
        dir = dir.parentFile
    }
    error("could not locate repo root (settings.gradle.kts) from ${System.getProperty("user.dir")}")
}

// --- canonical paths into the rest of the repo ---------------------------------------------------
val SDK_EFFECTS: File get() = File(REPO_ROOT, "mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/scripting/effects")
val SDK_ROOT: File get() = File(REPO_ROOT, "mtg-sdk/src/main/kotlin")
val KEYWORD_KT: File get() = File(REPO_ROOT, "mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/core/Keyword.kt")
val SUBTYPE_KT: File get() = File(REPO_ROOT, "mtg-sdk/src/main/kotlin/com/wingedsheep/sdk/core/Subtype.kt")
// Card definitions are split across `:mtg-sets:core` (setless cards) and one `:mtg-sets:<era>`
// module per fixed release-year range, so no single Kotlin compilation holds the whole corpus.
// There is no single definitions root any more — use these instead of composing a path by hand,
// and a newly added era module is picked up with no change here.
private const val DEFINITIONS_SUFFIX = "src/main/kotlin/com/wingedsheep/mtg/sets/definitions"

/** Every card module's `definitions/` directory, core first then era modules oldest to newest. */
val DEFINITIONS_ROOTS: List<File>
    get() = File(REPO_ROOT, "mtg-sets").listFiles { f: File -> f.isDirectory }
        .orEmpty()
        .map { File(it, DEFINITIONS_SUFFIX) }
        .filter { it.isDirectory }
        // "core" sorts after "2026" alphabetically, but it holds the cards that belong to no
        // release and every era can see, so it comes first.
        .sortedWith(compareBy({ if (it.parentFile.name == "core") 0 else 1 }, { it.path }))

/** The `definitions/` directory that owns `setDir`, or the newest era module for an unknown set. */
fun definitionsRootFor(setDir: String): File =
    DEFINITIONS_ROOTS.firstOrNull { File(it, setDir).isDirectory } ?: DEFINITIONS_ROOTS.last()
val SNAP_DIR: File get() = File(REPO_ROOT, "mtg-sets/src/test/resources/snapshots/cards")
val GEN_DIR: File get() = File(REPO_ROOT, "mtg-sets/build/generated-cards")

// --- this module's own working files (both gitignored; see mtgish-tooling/.gitignore) ------------
// The 29MB mtgish IR corpus is auto-downloaded into data/ on first run; generated/ is the draft
// staging dir for `autogen --write`.
val TOOLING_DIR: File get() = File(REPO_ROOT, "mtgish-tooling")
val MTGISH_LINES: File get() = File(TOOLING_DIR, "data/mtgish.lines.json")
const val MTGISH_URL = "https://raw.githubusercontent.com/i5jb/mtgish/main/data/mtgish.lines.json"
val DEFAULT_GENERATED_ROOT: File get() = File(TOOLING_DIR, "generated")

// --- set code -> definitions/ directory -----------------------------------------------------------

/**
 * Windows reserves the DOS device names (CON, PRN, AUX, NUL, COM1–9, LPT1–9) as file names — even
 * with an extension — and Git for Windows refuses to check such paths out.
 */
val WINDOWS_RESERVED_NAMES = Regex("(?i)^(CON|PRN|AUX|NUL|COM[0-9]|LPT[0-9])$")

/** [name] unchanged unless it's a Windows-reserved device name, which gets a trailing underscore. */
fun windowsSafeFileName(name: String): String =
    if (WINDOWS_RESERVED_NAMES.matches(name)) "${name}_" else name

/** Inverse of [windowsSafeFileName], for recovering a code from a filename on disk. */
fun fromWindowsSafeFileName(name: String): String =
    if (name.endsWith("_") && WINDOWS_RESERVED_NAMES.matches(name.dropLast(1))) name.dropLast(1) else name

private val SET_CODE_DECL = Regex("""override\s+val\s+code\s*=\s*"([^"]+)"""")

private val setDirByCode: Map<String, String> by lazy {
    DEFINITIONS_ROOTS.flatMap { it.listFiles { f: File -> f.isDirectory }.orEmpty().asList() }
        .sortedBy { it.name }
        .mapNotNull { dir ->
            dir.listFiles { f: File -> f.name.endsWith("Set.kt") }.orEmpty().sortedBy { it.name }
                .firstNotNullOfOrNull { SET_CODE_DECL.find(it.readText())?.groupValues?.get(1)?.lowercase() }
                ?.let { code -> code to dir.name }
        }
        .toMap()
}

/**
 * The `definitions/<dir>` (and package) segment for a set code. Usually the lowercase code, but
 * the directory name is not guaranteed: `con` is Windows-reserved, so Conflux lives in
 * `definitions/conflux/`. Scaffolded sets resolve to their actual directory (read from the
 * `*Set.kt` `code` declaration); unscaffolded reserved codes fall back to the underscore escape
 * so generated paths stay creatable on Windows.
 */
fun setDirSegment(setCode: String): String {
    val code = setCode.lowercase()
    return setDirByCode[code] ?: windowsSafeFileName(code)
}

// ---------------------------------------------------------------------------
// JsonElement accessors (the dict/list/scalar lens onto the dynamic IR).
// ---------------------------------------------------------------------------
val JsonElement?.asObj: JsonObject? get() = this as? JsonObject
val JsonElement?.asArr: JsonArray? get() = this as? JsonArray

/** String content iff this is a JSON string primitive, else null. */
fun JsonElement?.asStr(): String? = (this as? JsonPrimitive)?.takeIf { it.isString }?.content

/** Int iff this is a numeric primitive. */
fun JsonElement?.asInt(): Int? = (this as? JsonPrimitive)?.takeIf { !it.isString }?.content?.toIntOrNull()

/** `node.get(key)` for an object, else null. */
fun JsonElement?.field(key: String): JsonElement? = this.asObj?.get(key)

/** `node.get(key)` returning its string value, else null — the most common IR read. */
fun JsonElement?.strField(key: String): String? = field(key).asStr()

/** Compact `json.dumps(node)` equivalent for the regex-on-blob helpers. */
fun compact(node: JsonElement?): String = if (node == null) "null" else J.encodeToString(JsonElement.serializer(), node)

/**
 * Card name → PascalCase ASCII identifier, matching the hand-authored convention used for `val`
 * names and source files: accents are transliterated (`Déjà Vu` → `DejaVu`), spaces and hyphens are
 * word boundaries that capitalize the next word (`Path of Peace` → `PathOfPeace`,
 * `Troll-Horn Cameo` → `TrollHornCameo`), and apostrophes are dropped *without* a boundary so the
 * trailing fragment stays joined (`Esika's Chariot` → `EsikasChariot`).
 */
fun asciiIdentifier(name: String): String = Normalizer.normalize(name, Normalizer.Form.NFD)
    .replace(Regex("\\p{M}+"), "")        // strip accents (combining marks left by NFD)
    .replace(Regex("['’]"), "")      // apostrophes merge into the word — no capitalization break
    .split(Regex("[^A-Za-z0-9]+"))        // every other non-alphanumeric run is a word boundary
    .filter { it.isNotEmpty() }
    .joinToString("") { it.replaceFirstChar(Char::uppercaseChar) }

// ---------------------------------------------------------------------------
// Tree walkers — the recursive helpers that read the dynamic IR.
// ---------------------------------------------------------------------------

/** `_contains(node, key, val)` — does the subtree carry a `{key: val}` (string) pair anywhere? */
fun jsonContains(node: JsonElement?, key: String, value: String): Boolean = when (node) {
    is JsonObject -> node.strField(key) == value || node.values.any { jsonContains(it, key, value) }
    is JsonArray -> node.any { jsonContains(it, key, value) }
    else -> false
}

/** `_find_integer` — first Integer/XValue under the subtree. Returns Int, the string "X", or null. */
fun findInteger(node: JsonElement?): Any? {
    when (node) {
        is JsonObject -> {
            when (node.strField("_GameNumber")) {
                "Integer" -> return node["args"].asInt()
                "XValue", "X", "ValueX" -> return "X"
            }
            for (v in node.values) findInteger(v)?.let { return it }
        }
        is JsonArray -> for (v in node) findInteger(v)?.let { return it }
        else -> {}
    }
    return null
}

/** `_find_adjust_pt` — the args (a [p, t] array) of the first AdjustPT layer effect. */
fun findAdjustPt(node: JsonElement?): JsonElement? {
    when (node) {
        is JsonObject -> {
            if (node.strField("_LayerEffect") == "AdjustPT") node["args"]?.let { return it }
            for (v in node.values) findAdjustPt(v)?.let { return it }
        }
        is JsonArray -> for (v in node) findAdjustPt(v)?.let { return it }
        else -> {}
    }
    return null
}

/** `_subtypes` — IsLandType / IsCardSubtype / library IsLandType argument strings in a filter subtree. */
fun subtypes(node: JsonElement?): List<String> {
    val out = mutableListOf<String>()
    fun walk(n: JsonElement?) {
        when (n) {
            is JsonObject -> {
                if (n.strField("_Permanents") in setOf("IsLandType", "IsCardSubtype")) n["args"].asStr()?.let { out.add(it) }
                if (n.strField("_CardsInLibrary") == "IsLandType") n["args"].asStr()?.let { out.add(it) }
                n.values.forEach { walk(it) }
            }
            is JsonArray -> n.forEach { walk(it) }
            else -> {}
        }
    }
    walk(node)
    return out
}

/** `_find_ref` — first _Permanent / _Player / _CardInGraveyards reference string in a subtree. */
fun findRef(node: JsonElement?): String? {
    var found: String? = null
    fun walk(n: JsonElement?) {
        if (found != null) return
        when (n) {
            is JsonObject -> {
                for (k in listOf("_Permanent", "_Player", "_CardInGraveyards")) {
                    if (found == null) n.strField(k)?.let { found = it }
                }
                n.values.forEach { walk(it) }
            }
            is JsonArray -> n.forEach { walk(it) }
            else -> {}
        }
    }
    walk(node)
    return found
}

/** First reference under the object marked by [markerKey], e.g. a `_DamageRecipient` node. */
fun findRefIn(node: JsonElement?, markerKey: String): String? {
    var found: String? = null
    fun walk(n: JsonElement?) {
        if (found != null) return
        when (n) {
            is JsonObject -> {
                if (n.containsKey(markerKey)) {
                    found = findRef(n[markerKey]) ?: findRef(n["args"])
                    if (found != null) return
                }
                n.values.forEach { walk(it) }
            }
            is JsonArray -> n.forEach { walk(it) }
            else -> {}
        }
    }
    walk(node)
    return found
}

/** `_amount_node` — first subtree object carrying a `_GameNumber` (an amount expression). */
fun amountNode(args: JsonElement?): JsonElement? {
    var found: JsonElement? = null
    fun walk(n: JsonElement?) {
        if (found != null) return
        when (n) {
            is JsonObject -> {
                if (found == null && n.containsKey("_GameNumber")) found = n
                n.values.forEach { walk(it) }
            }
            is JsonArray -> n.forEach { walk(it) }
            else -> {}
        }
    }
    walk(args)
    return found
}

/** `pascal_to_upper_snake` — FirstStrike -> FIRST_STRIKE, Shadow -> SHADOW. */
private val SNAKE_BOUNDARY = Regex("(?<=[a-z0-9])(?=[A-Z])")
fun pascalToUpperSnake(s: String): String = SNAKE_BOUNDARY.replace(s, "_").uppercase()
