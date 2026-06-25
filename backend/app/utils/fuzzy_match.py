from difflib import SequenceMatcher

def similarity(a: str, b: str) -> float:
    return SequenceMatcher(None, a.lower(), b.lower()).ratio()


def best_match(query: str, choices: list):
    best = None
    best_score = 0

    for c in choices:
        score = similarity(query, c)
        if score > best_score:
            best = c
            best_score = score

    return best, best_score