import os
import subprocess
import json

# ---------------- CONFIG ----------------
DATASET_ROOT = "UnprocessedEmotionAudios"
MOVIE_NAME = "Tangled"   # <--- choose the movie to process

GENDERS = ["Male", "Female"]
EMOTIONS = [
    "Anger","Calm", "Sad", "Fear", "Warning", "Surprise",
    "Whisper", "Excitement","Horror", "Flirty", "Pained", "Narration"
]

OUTPUT_ROOT = "EmotionAudios"
COUNTER_FILE = os.path.join(OUTPUT_ROOT, "counters.json")
# ----------------------------------------

def load_counters():
    if os.path.exists(COUNTER_FILE):
        with open(COUNTER_FILE, "r") as f:
            return json.load(f)
    return {}

def save_counters(counters):
    os.makedirs(OUTPUT_ROOT, exist_ok=True)
    with open(COUNTER_FILE, "w") as f:
        json.dump(counters, f, indent=2)

def process_audio(input_path, output_path):
    """
    Convert audio to wav, mono, 24kHz, normalized to -20 LUFS.
    """
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    cmd = [
        "ffmpeg",
        "-i", input_path,
        "-ar", "24000",     # resample to 24kHz
        "-ac", "1",         # mono
        "-af", "loudnorm=I=-20:LRA=7:TP=-2",  # normalize loudness
        output_path,
        "-y"  # overwrite
    ]
    subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)

def main():
    counters = load_counters()

    # Only look inside this movie’s folder
    movie_root = os.path.join(DATASET_ROOT, MOVIE_NAME)

    for gender in GENDERS:
        for emotion in EMOTIONS:
            counters.setdefault(f"{gender}_{emotion}", 0)

            input_dir = os.path.join(movie_root, gender, emotion)
            if not os.path.exists(input_dir):
                continue

            for file in os.listdir(input_dir):
                if file.lower().endswith((".wav", ".m4a", ".mp3", ".flac")):
                    input_path = os.path.join(input_dir, file)

                    # Increment counter for this gender/emotion
                    counters[f"{gender}_{emotion}"] += 1
                    new_name = f"{counters[f'{gender}_{emotion}']}.wav"

                    # Output path (without movie name)
                    output_dir = os.path.join(OUTPUT_ROOT, gender, emotion)
                    output_path = os.path.join(output_dir, new_name)

                    print(f"Processing {input_path} -> {output_path}")
                    process_audio(input_path, output_path)

    save_counters(counters)
    print(f"✅ All audio files from {MOVIE_NAME} processed with continuous numbering!")

if __name__ == "__main__":
    main()
