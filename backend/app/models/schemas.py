from pydantic import BaseModel
from typing import Optional, Dict, Any


class ExtractResponse(BaseModel):
    text: str
    issuer: str
    title: str


class SourceRequest(BaseModel):
    issuer: str
    title: str


class SourceResponse(BaseModel):
    issuer: str
    official_source: Optional[str]
    confidence: float


class ProcessResponse(BaseModel):
    text_preview: str
    issuer: str
    title: str
    source: Dict[str, Any]