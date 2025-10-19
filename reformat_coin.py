from PIL import Image

def center_in_30x30(input_path, output_path):
    # Open the source image
    img = Image.open(input_path).convert("RGBA")

    # Create a 30x30 transparent background
    background = Image.new("RGBA", (30, 30), (0, 0, 0, 0))

    # Calculate position to center the image
    x = (30 - img.width) // 2
    y = (30 - img.height) // 2

    # Paste the image onto the transparent background
    background.paste(img, (x, y), img)

    # Save the new image
    background.save(output_path)

# Example usage
center_in_30x30("coin.png", "coin.png")

