from fastapi import APIRouter
from app.services.source_service import find_official_source

router = APIRouter()

@router.post("/")
def source_discovery(payload: dict):
    issuer = payload.get("issuer")
    title = payload.get("title")

    print("Issuer received:", issuer)
    print("Title received:", title)

    result = find_official_source(issuer, title)

    print("Source result:", result)

    return result