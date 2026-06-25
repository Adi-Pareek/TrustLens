from fastapi import APIRouter, UploadFile, File
import pdfplumber
import io

from app.services.source_service import find_official_source
from app.utils.text_cleaning import clean_text
from app.core.config import Config

router = APIRouter()

# ---------------- OCR ----------------
def extract_text(file_bytes):
    text = ""

    with pdfplumber.open(io.BytesIO(file_bytes)) as pdf:
        for page in pdf.pages:
            text += page.extract_text() or ""

    return text


# ---------------- ISSUER DETECTION ----------------
def get_issuer(text):
    for issuer in Config.SUPPORTED_ISSUERS:
        if issuer.lower() in text.lower():
            return issuer
    return "Unknown"


# ---------------- TITLE ----------------
def get_title(text):
    return text[:80].replace("\n", " ").strip()


# ---------------- MAIN API ----------------
@router.post("/")
async def process_document(file: UploadFile = File(...)):
    content = await file.read()

    # Step 1: Extract text
    raw_text = extract_text(content)

    # Step 2: Clean text
    text = clean_text(raw_text)

    # Step 3: Metadata
    issuer = get_issuer(text)
    title = get_title(text)

    # Step 4: Source discovery
    source = find_official_source(issuer, title)

    return {
        "text_preview": text[:1000],
        "issuer": issuer,
        "title": title,
        "source": source
    }