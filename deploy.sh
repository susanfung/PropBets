#!/bin/bash
# Quick Deployment Script for PropBets on Render
# Run this to commit and push your changes

echo "🚀 PropBets - Render Deployment Helper"
echo "========================================"
echo ""

# Check if we're in the right directory
if [ ! -f "Dockerfile" ] || [ ! -f "render.yaml" ]; then
    echo "❌ Error: Not in PropBets directory"
    echo "Please run: cd /Users/susanfung/Documents/GitHub/PropBets"
    exit 1
fi

echo "✅ In correct directory"
echo ""

# Show what will be committed
echo "📦 Files to commit:"
echo "  - Dockerfile (system Maven fix)"
echo "  - render.yaml (docker runtime)"
echo "  - .dockerignore (build optimization)"
echo "  - Documentation files"
echo ""

# Stage files
echo "📝 Staging files..."
git add Dockerfile render.yaml .dockerignore
git add RENDER_DEPLOYMENT.md PRE_DEPLOYMENT_CHECKLIST.md FINAL_STATUS.md
echo "✅ Files staged"
echo ""

# Show status
echo "📊 Git status:"
git status --short
echo ""

# Commit
echo "💾 Creating commit..."
git commit -m "Fix: Use system Maven to resolve /root/.m2 build error

- Changed from Maven wrapper (./mvnw) to system Maven (mvn)
- Eliminates configuration conflict causing '/root/.m2' error
- Simplifies Dockerfile and reduces complexity
- Maintains multi-stage build for optimal image size
- Ready for Render deployment with docker runtime"

if [ $? -eq 0 ]; then
    echo "✅ Commit created successfully"
    echo ""

    # Push
    echo "🌐 Pushing to GitHub..."
    read -p "Push to origin main? (y/n) " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        git push origin main
        if [ $? -eq 0 ]; then
            echo ""
            echo "✅ Successfully pushed to GitHub!"
            echo ""
            echo "🎉 Next Steps:"
            echo "1. Go to https://render.com/dashboard"
            echo "2. Click 'New +' → 'Web Service'"
            echo "3. Connect your PropBets repository"
            echo "4. Render will auto-detect render.yaml"
            echo "5. Click 'Create Web Service'"
            echo ""
            echo "⏱️  Build time: ~10 minutes"
            echo "🌍 Your app will be at: https://propbets.onrender.com"
            echo ""
            echo "✅ Deployment configuration is ready!"
        else
            echo "❌ Push failed. Please check your git configuration."
        fi
    else
        echo "⏸️  Push skipped. Run 'git push origin main' when ready."
    fi
else
    echo "❌ Commit failed. Please check git status."
fi

echo ""
echo "📚 Documentation:"
echo "  - FINAL_STATUS.md - Current status and overview"
echo "  - RENDER_DEPLOYMENT.md - Complete deployment guide"
echo "  - PRE_DEPLOYMENT_CHECKLIST.md - Step-by-step checklist"

