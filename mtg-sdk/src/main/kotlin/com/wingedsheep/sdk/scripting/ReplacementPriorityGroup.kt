package com.wingedsheep.sdk.scripting

/**
 * Priority group for classifying replacement effects per CR 616.1a-f.
 *
 * Each [ReplacementEffect] subtype declares its own [ReplacementEffect.priorityGroup]
 * so the engine processor never needs to pattern-match on SDK types.
 *
 * WARNING: declaration order is load-bearing — [enumEntries] drives
 * CR 616.1a→e processing in [com.wingedsheep.engine.replacement.ReplacementEffectProcessor].
 * Do not reorder entries. Insert new entries in their correct priority
 * position per the CR rule number (a → e).
 */
enum class ReplacementPriorityGroup {
    /** Self-replacement effects (CR 616.1a) — must be chosen first */
    SELF_REPLACEMENT,
    /** Control-changing effects (CR 616.1b) */
    CONTROL_CHANGE,
    /** Copy effects (CR 616.1c) */
    COPY,
    /** Transform effects (CR 616.1d) — replacements that cause entering with back face up */
    TRANSFORM,
    /** All other replacement effects (CR 616.1e) — affected player may choose any */
    ANY
}
