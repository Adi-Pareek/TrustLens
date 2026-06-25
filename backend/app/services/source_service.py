OFFICIAL_SITES = {
    "Tata": "https://www.tatamotors.com/investors/",
    "Google": "https://about.google/",
    "Microsoft": "https://www.microsoft.com/",
    "Amazon": "https://www.aboutamazon.com/",
    "Infosys": "https://www.infosys.com/"
}

def find_official_source(issuer, title):

    if issuer in OFFICIAL_SITES:
        return {
            "issuer": issuer,
            "official_source": OFFICIAL_SITES[issuer],
            "confidence": 0.9
        }

    return {
        "issuer": issuer,
        "official_source": None,
        "confidence": 0.0
    }