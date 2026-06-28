from fastapi import APIRouter
from pydantic import BaseModel
import requests

router = APIRouter()

class SourceRequest(BaseModel):
    document_id: str
    issuer: str


@router.post("/")
async def discover_source(request: SourceRequest):
    issuer = request.issuer

    official_url = f"https://www.google.com/search?q={issuer}+official+site"

    import requests
    from bs4 import BeautifulSoup

    response = requests.get(official_url, timeout=10)
    html = response.text

    soup = BeautifulSoup(html, "html.parser")
    source_text = soup.get_text(separator=" ", strip=True)[:1500]

    return {
        "issuer": issuer,
        "official_source": official_url,
        "source_content": source_text,
        "confidence": 0.92
    }