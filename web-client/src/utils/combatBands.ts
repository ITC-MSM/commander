import type { EntityId } from '@/types'

/**
 * The band that linking [sourceId] to [targetId] would produce (CR 702.22): both creatures plus
 * every member of whichever bands they already belong to.
 */
export function mergedBand(
  bands: readonly (readonly EntityId[])[],
  sourceId: EntityId,
  targetId: EntityId,
): EntityId[] {
  const sourceBand = bands.find((b) => b.includes(sourceId)) ?? []
  const targetBand = bands.find((b) => b.includes(targetId)) ?? []
  return Array.from(new Set<EntityId>([sourceId, targetId, ...sourceBand, ...targetBand]))
}

/**
 * CR 702.22c: a band may contain at most one creature without banding — which also covers the
 * two-creature case where neither has it. `hasBanding` should read the server's projected
 * keyword set (`ClientCard.keywords`), the same set the engine's `validateBands` reads, so the
 * client only ever refuses a band the server would refuse too.
 */
export function bandIsLegal(members: readonly EntityId[], hasBanding: (id: EntityId) => boolean): boolean {
  return members.filter((id) => !hasBanding(id)).length <= 1
}
