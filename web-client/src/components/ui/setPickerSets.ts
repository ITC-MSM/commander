import type { AvailableSet } from '@/types/messages'

/** Search includes partial and small pools so an explicitly sought set remains discoverable. */
export function visiblePickerSets(
  sets: readonly AvailableSet[],
  selectedCodes: readonly string[],
  options: { search: string; single: boolean; showPartial: boolean; minCards: number },
): readonly AvailableSet[] {
  const needle = options.search.trim().toLowerCase()
  return sets.filter((set) => {
    if (options.single && set.extensionSet) return false
    if (needle) return set.name.toLowerCase().includes(needle) || set.code.toLowerCase().includes(needle)
    if (selectedCodes.includes(set.code)) return true
    if (set.partial && !options.showPartial) return false
    return set.extensionSet || (set.implementedCount ?? 0) >= options.minCards
  })
}
