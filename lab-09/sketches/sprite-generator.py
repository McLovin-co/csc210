from PIL import Image, ImageDraw

# Create a new 64x64 image with a transparent background
sprite = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
draw = ImageDraw.Draw(sprite)

# Draw the head (an ellipse) with a skin tone and a black outline
head_box = (24, 8, 40, 24)
draw.ellipse(head_box, fill=(255, 220, 177, 255), outline="black")

# Add a hair band to the head for a bit of flair
hair_box = (24, 8, 40, 16)
draw.rectangle(hair_box, fill="brown", outline="black")

# Draw the torso as a rectangle (blue with a black outline)
body_box = (20, 24, 44, 44)
draw.rectangle(body_box, fill="blue", outline="black")

# Draw the arms (left and right) as rectangles matching the torso's color
# Left arm
left_arm_box = (12, 24, 20, 40)
draw.rectangle(left_arm_box, fill="blue", outline="black")
# Right arm
right_arm_box = (44, 24, 52, 40)
draw.rectangle(right_arm_box, fill="blue", outline="black")

# Draw the legs as rectangles (brown with black outlines)
# Left leg
left_leg_box = (22, 44, 30, 64)
draw.rectangle(left_leg_box, fill="brown", outline="black")
# Right leg
right_leg_box = (34, 44, 42, 64)
draw.rectangle(right_leg_box, fill="brown", outline="black")

# Add a simple sword to give it an extra videogame touch
# Sword blade: a silver line
draw.line([(52, 28), (60, 40)], fill="silver", width=2)
# Sword hilt: a small gold rectangle with a black outline
draw.rectangle((50, 26, 52, 30), fill="gold", outline="black")

# Save the sprite to a file named "sprite.png"
sprite.save("sprite.png")
sprite.show()  # Opens the image if your environment supports it

