from fastapi import FastAPI
from app.api import extract, source_discovery, process

app = FastAPI(title="Member 3 Backend")

app.include_router(extract.router, prefix="/extract", tags=["OCR"])
app.include_router(source_discovery.router, prefix="/source-discovery", tags=["Source"])
app.include_router(process.router, prefix="/process-document", tags=["Full Pipeline"])


@app.get("/")
def home():
    return {"message": "Backend running"}