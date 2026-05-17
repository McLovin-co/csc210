from PIL import Image, ImageDraw

# Create a new image with dimensions 256x64 (4 frames of 64x64)
sprite_sheet = Image.new("RGBA", (256, 64), (255, 255, 255, 0))

for frame in range(4):
    offset = frame * 64  # X offset for each frame
    draw = ImageDraw.Draw(sprite_sheet)
    
    # Draw head: yellow circle with a black outline
    head_bbox = (offset + 22, 5, offset + 42, 25)  # Bounding box for the head
    draw.ellipse(head_bbox, fill="yellow", outline="black")
    
    # Draw body: vertical line
    draw.line([(offset + 32, 25), (offset + 32, 45)], fill="black", width=2)
    
    # Draw arms with slight variations for each frame
    if frame == 0:
        # Arms horizontal
        draw.line([(offset + 32, 30), (offset + 22, 30)], fill="black", width=2)
        draw.line([(offset + 32, 30), (offset + 42, 30)], fill="black", width=2)
    elif frame == 1:
        # Both arms raised
        draw.line([(offset + 32, 30), (offset + 22, 20)], fill="black", width=2)
        draw.line([(offset + 32, 30), (offset + 42, 20)], fill="black", width=2)
    elif frame == 2:
        # Both arms lowered
        draw.line([(offset + 32, 30), (offset + 22, 40)], fill="black", width=2)
        draw.line([(offset + 32, 30), (offset + 42, 40)], fill="black", width=2)
    elif frame == 3:
        # One arm raised (left) and one lowered (right)
        draw.line([(offset + 32, 30), (offset + 22, 20)], fill="black", width=2)
        draw.line([(offset + 32, 30), (offset + 42, 40)], fill="black", width=2)
    
    # Draw legs (same for all frames)
    draw.line([(offset + 32, 45), (offset + 22, 55)], fill="black", width=2)
    draw.line([(offset + 32, 45), (offset + 42, 55)], fill="black", width=2)

# Save the sprite sheet to a file named "spriteSheet.png"
sprite_sheet.save("spriteSheet.png")
sprite_sheet.show()  # This will open the image if your environment supports it

