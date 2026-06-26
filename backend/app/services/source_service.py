import requests
from bs4 import BeautifulSoup

def find_official_source(issuer, title):

    if not issuer:
        return {
            "issuer": "Unknown",
            "official_source": None,
            "confidence": 0.0
        }

    query = f"{issuer} official website"

    url = f"https://www.google.com/search?q={query}"

    headers = {
        "User-Agent": "Mozilla/5.0"
    }

    response = requests.get(url, headers=headers)

    if response.status_code != 200:
        return {
            "issuer": issuer,
            "official_source": None,
            "confidence": 0.0
        }

    soup = BeautifulSoup(response.text, "html.parser")

    links = []
    for a in soup.find_all("a"):
        href = a.get("href")
        if href and "http" in href:
            links.append(href)

    official_link = links[0] if links else None

    return {
        "issuer": issuer,
        "official_source": official_link,
        "confidence": 0.8 if official_link else 0.0
    }