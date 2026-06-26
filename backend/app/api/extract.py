from fastapi import APIRouter, UploadFile, File
import pdfplumber
import io
import os
import google.generativeai as genai

router = APIRouter()

# Load API key from Render environment
genai.configure(api_key=os.getenv("GEMINI_API_KEY"))

model = genai.GenerativeModel("gemini-2.5-flash")


def get_issuer(text):
    prompt = f"""
    Extract the company/organization/issuer name from this document.
    Return only the issuer name.

    Document:
    {text[:1000]}
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