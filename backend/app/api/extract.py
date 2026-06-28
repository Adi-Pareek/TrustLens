from fastapi import APIRouter, UploadFile, File
import pdfplumber
import io
import os
import google.generativeai as genai
from pdf2image import convert_from_bytes
import pytesseract

router = APIRouter()

# Configure Gemini API
genai.configure(api_key=os.getenv("GEMINI_API_KEY"))

# Configure Tesseract path (Windows)
pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files\Tesseract-OCR\tesseract.exe"

# Gemini model
model = genai.GenerativeModel("gemini-2.5-flash")


ddef get_issuer(text):
     try:
         prompt = f"""
         Identify the MAIN issuing organization of this document.

         Rules:
         1. Return ONLY the organization/hospital/company/university name.
         2. Ignore patient names, doctor names, medicine names.
         3. If hospital prescription, return hospital name.
         4. If government document, return department name.
         5. Return only one name.

         Document:
         {text[:2000]}
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

            # Step 1: Try normal text extraction
            with pdfplumber.open(io.BytesIO(content)) as pdf:
                for page in pdf.pages:
                    text += page.extract_text() or ""

            # Step 2: OCR fallback for scanned PDFs
            if not text.strip():
                print("No selectable text found. Running OCR...")

                images = convert_from_bytes(content)

                for img in images:
                    text += pytesseract.image_to_string(img)

        # Image files OCR
        elif file.filename.endswith((".jpg", ".jpeg", ".png")):
            text = pytesseract.image_to_string(io.BytesIO(content))

        # No text found
        if not text.strip():
            return {
                "error": "No text extracted from document"
            }

        issuer = get_issuer(text)

        return {
            "text": text[:1000],
            "issuer": issuer,
            "title": text[:80] if text else "Untitled"
        }

    except Exception as e:
        print("Extraction error:", e)
        return {
            "error": str(e)
        }