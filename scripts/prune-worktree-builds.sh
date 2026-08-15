#!/usr/bin/env bash
#
# prune-worktree-builds.sh — reclaim disk from `build/` and `.gradle/` directories in git
# worktrees that nobody is working in any more.
#
# Why: every worktree is a separate Gradle root with its own outputs. On this box 20 live
# worktrees held ~15 GB of `build/`, much of it from branches merged weeks earlier. That is
# disk, but it also means every stale worktree is a potential extra Gradle daemon root.
#
# SAFETY — this deletes build output belonging to *other agents' worktrees*, so it refuses to
# touch anything that might be in flight (see AGENTS.md "Focus on your own work"):
#   * the current worktree is never touched;
#   * a worktree whose *sources* were modified within --days is skipped (someone is working there);
#   * a worktree named in the command line of a running java/gradle process is skipped;
#   * if any gradle-locked semaphore slot is held, the whole run is skipped — a build is active
#     somewhere and we cannot tell which worktree it belongs to.
# It is a DRY RUN unless --apply is passed.
#
# Usage:
#   scripts/prune-worktree-builds.sh                 # dry run, 7-day threshold
#   scripts/prune-worktree-builds.sh --days 14 --apply

set -euo pipefail

DAYS=7
APPLY=0
LOCK_FILE="${GRADLE_LOCK_FILE:-$HOME/.cache/argentum/gradle.lock}"

while [ $# -gt 0 ]; do
    case "$1" in
        --days) DAYS="$2"; shift 2 ;;
        --apply) APPLY=1; shift ;;
        --dry-run) APPLY=0; shift ;;
        -h|--help) sed -n '2,25p' "$0"; exit 0 ;;
        *) echo "prune-worktree-builds: unknown argument '$1'" >&2; exit 2 ;;
    esac
done

if ! [[ "$DAYS" =~ ^[0-9]+$ ]]; then
    echo "prune-worktree-builds: --days must be an integer (got '$DAYS')." >&2
    exit 2
fi

# A build in flight anywhere means we cannot safely reason about which outputs are live.
for slot in "$LOCK_FILE" "$LOCK_FILE".[0-9]; do
    if [ -e "$slot" ]; then
        echo "prune-worktree-builds: '$slot' is held — a Gradle build is running. Try again later." >&2
        exit 0
    fi
done

CURRENT="$(git rev-parse --show-toplevel)"
BUSY_PROCS="$(ps -Ao command= | grep -E 'java|gradle' | grep -v grep || true)"

total_kb=0
pruned_kb=0

while IFS= read -r wt; do
    [ -n "$wt" ] || continue

    # Collect this worktree's Gradle output directories.
    outs=()
    [ -d "$wt/build" ] && outs+=("$wt/build")
    [ -d "$wt/.gradle" ] && outs+=("$wt/.gradle")
    for d in "$wt"/*/build; do
        [ -d "$d" ] && outs+=("$d")
    done
    [ ${#outs[@]} -gt 0 ] || continue

    size_kb=$(du -sk "${outs[@]}" 2>/dev/null | awk '{t+=$1} END{print t+0}')
    total_kb=$((total_kb + size_kb))
    size_h=$(awk -v k="$size_kb" 'BEGIN{printf "%.1f GB", k/1024/1024}')

    if [ "$wt" = "$CURRENT" ]; then
        echo "skip  $size_h  $wt  (current worktree)"
        continue
    fi

    # Recently touched sources => someone is working here.
    recent=$(find "$wt" -type f \
        \( -name '*.kt' -o -name '*.kts' -o -name '*.ts' -o -name '*.tsx' -o -name '*.json' -o -name '*.md' \) \
        -not -path '*/build/*' -not -path '*/.gradle/*' -not -path '*/node_modules/*' -not -path '*/.git/*' \
        -mtime "-${DAYS}" -print -quit 2>/dev/null || true)
    if [ -n "$recent" ]; then
        echo "skip  $size_h  $wt  (sources modified in the last ${DAYS}d)"
        continue
    fi

    if printf '%s\n' "$BUSY_PROCS" | grep -qF -- "$wt"; then
        echo "skip  $size_h  $wt  (a running java/gradle process references it)"
        continue
    fi

    pruned_kb=$((pruned_kb + size_kb))
    if [ "$APPLY" -eq 1 ]; then
        echo "prune $size_h  $wt"
        rm -rf "${outs[@]}"
    else
        echo "would $size_h  $wt"
    fi
done < <(git worktree list --porcelain | awk '/^worktree /{print substr($0, 10)}')

echo
awk -v t="$total_kb" -v p="$pruned_kb" -v a="$APPLY" 'BEGIN{
    printf "build output across all worktrees: %.1f GB\n", t/1024/1024
    printf "%s: %.1f GB\n", (a ? "reclaimed" : "reclaimable (re-run with --apply)"), p/1024/1024
}'
