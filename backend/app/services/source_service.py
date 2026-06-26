import requests
from bs4 import BeautifulSoup

def find_official_source(issuer, title):
    query = f"{issuer} official website"

    url = f"https://www.google.com/search?q={query}"

    headers = {
        "User-Agent": "Mozilla/5.0"
    }

    response = requests.get(url, headers=headers)

    soup = BeautifulSoup(response.text, "html.parser")

    for link in soup.find_all("a"):
        href = link.get("href")

        if href and "url?q=" in href:
            official_link = href.split("url?q=")[1].split("&")[0]

            return {
                "issuer": issuer,
                "official_source": official_link,
                "confidence": 0.85
            }

    return {
        "issuer": issuer,
        "official_source": None,
        "confidence": 0.0
    }