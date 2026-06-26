from fastapi import APIRouter, UploadFile, File
import pdfplumber
import io
import os
import google.generativeai as genai

router = APIRouter()

# Configure Gemini API
genai.configure(api_key=os.getenv("GEMINI_API_KEY"))

# Gemini model
model = genai.GenerativeModel("gemini-2.5-flash")


def get_issuer(text):
    try:
        prompt = f"""
        Extract the company, organization, hospital, university, or issuer name
        from this document.

        Return only the issuer name.

        Document:
        {text[:1000]}
        """

        response = model.generate_content(prompt)

        if response.text and response.text.strip():
            return response.text.strip()

        return "Unknown"

    except Exception as e:
        print("Issuer extraction error:", e)
        return "Unknown"


@router.post("/")
async def extract(file: UploadFile = File(...)):
    try:
        content = await file.read()
        text = ""

        # PDF extraction
        if file.filename.endswith(".pdf"):
            with pdfplumber.open(io.BytesIO(content)) as pdf:
                for page in pdf.pages:
                    text += page.extract_text() or ""

        # Image support placeholder
        elif file.filename.endswith((".jpg", ".jpeg", ".png")):
            return {
                "error": "Image OCR not implemented yet"
            }

        # If text empty
        if not text.strip():
            return {
                "error": "No text extracted from PDF"
            }

        issuer = get_issuer(text)

        return {
            "text": text[:1000],
            "issuer": issuer if issuer else "Unknown",
            "title": text[:80] if text else "Untitled"
        }

    except Exception as e:
        print("Extraction error:", e)
        return {
            "error": str(e)
        }