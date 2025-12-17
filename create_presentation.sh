#!/bin/bash

# Script to help create presentation from markdown
# Converts PRESENTATION_SLIDES.md to various formats

echo "KACM Food Recommendation - Presentation Generator"
echo "=================================================="
echo ""

# Check if pandoc is installed (for converting to PowerPoint/PDF)
if command -v pandoc &> /dev/null; then
    echo "✓ Pandoc found - Can convert to PowerPoint/PDF"
    echo ""
    echo "Converting to PowerPoint..."
    pandoc PRESENTATION_SLIDES.md -o PRESENTATION.pptx --reference-doc=template.pptx 2>/dev/null || \
    pandoc PRESENTATION_SLIDES.md -o PRESENTATION.pptx
    
    echo "Converting to PDF..."
    pandoc PRESENTATION_SLIDES.md -o PRESENTATION.pdf
    
    echo "Converting to HTML slides (reveal.js)..."
    pandoc PRESENTATION_SLIDES.md -o PRESENTATION.html -t revealjs -s
    
    echo ""
    echo "✓ Files created:"
    echo "  - PRESENTATION.pptx"
    echo "  - PRESENTATION.pdf"
    echo "  - PRESENTATION.html"
else
    echo "Pandoc not found. Install it for automatic conversion:"
    echo "  macOS: brew install pandoc"
    echo "  Or download from: https://pandoc.org/installing.html"
    echo ""
    echo "Alternative: Use the markdown file directly with:"
    echo "  - Marp (https://marp.app/)"
    echo "  - Reveal.js (https://revealjs.com/)"
    echo "  - Google Slides (import markdown)"
    echo "  - PowerPoint (copy/paste sections)"
fi

echo ""
echo "Presentation content is in: PRESENTATION_SLIDES.md"

