from fastapi import APIRouter, UploadFile, File
import pdfplumber
import io

router = APIRouter()

def get_issuer(text):
    keywords = ["Tata", "Google", "Microsoft", "Amazon", "Infosys", "IBM"]
    for k in keywords:
        if k.lower() in text.lower():
            return k
    return "Unknown"

@router.post("/")
async def extract(file: UploadFile = File(...)):
    content = await file.read()

    text = ""

    if file.filename.endswith(".pdf"):
        with pdfplumber.open(io.BytesIO(content)) as pdf:
            for page in pdf.pages:
                text += page.extract_text() or ""

    return {
        "text": text[:1000],
        "issuer": get_issuer(text),
        "title": text[:80]
    }