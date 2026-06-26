from fastapi import APIRouter, UploadFile, File
import pdfplumber
import google.generativeai as genai
import io

router = APIRouter()

# Configure Gemini
import os
genai.configure(api_key=os.getenv("GEMINI_API_KEY"))

def get_issuer(text):
    model = genai.GenerativeModel("gemini-2.0-flash")

    prompt = f"""
    Extract the issuing organization/company/hospital name from this document.
    Return only the issuer name.

    Document:
    {text[:2000]}
    """

    response = model.generate_content(prompt)

    return response.text.strip()


@router.post("/")
async def extract(file: UploadFile = File(...)):
    content = await file.read()

    text = ""

    if file.filename.endswith(".pdf"):
        with pdfplumber.open(io.BytesIO(content)) as pdf:
            for page in pdf.pages:
                text += page.extract_text() or ""

    issuer = get_issuer(text)

    return {
        "text": text[:1000],
        "issuer": issuer,
        "title": text[:80]
    }