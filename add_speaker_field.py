import json
import re

# Speaker mapping from Sanskrit to English
SPEAKER_MAP = {
    'धृतराष्ट्र': 'Dhritarashtra',
    'सञ्जय': 'Sanjaya',
    'संजय': 'Sanjaya',
    'अर्जुन': 'Arjuna',
    'श्री भगवान': 'Krishna',
    'श्रीभगवान': 'Krishna'
}

def extract_speaker(text):
    """Extract speaker from verse text if it contains 'उवाच' prefix."""
    if not text:
        return None
    
    # Check for each speaker pattern directly
    # Check for Krishna first (श्री भगवानुवाच without space between भगवान and उवाच)
    if 'भगवानुवाच' in text or 'भगवान उवाच' in text:
        return 'Krishna'
    elif 'धृतराष्ट्र उवाच' in text:
        return 'Dhritarashtra'
    elif 'अर्जुन उवाच' in text:
        return 'Arjuna'
    elif 'सञ्जय उवाच' in text or 'संजय उवाच' in text:
        return 'Sanjaya'
    
    return None

def add_speaker_field(input_file, output_file):
    """Add speaker field to all verses in the JSON."""
    # Read the JSON file
    with open(input_file, 'r', encoding='utf-8') as f:
        verses = json.load(f)
    
    # Default speaker (first verse in Gita)
    current_speaker = 'Dhritarashtra'
    
    # Process each verse
    for verse in verses:
        # Try to extract speaker from current verse
        detected_speaker = extract_speaker(verse.get('text', ''))
        
        # If speaker found, update current speaker
        if detected_speaker:
            current_speaker = detected_speaker
        
        # Add speaker field to verse
        verse['speaker'] = current_speaker
    
    # Write updated JSON to output file
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(verses, f, ensure_ascii=False, indent=2)
    
    print(f"✓ Successfully processed {len(verses)} verses")
    
    # Print speaker statistics
    speaker_counts = {}
    for verse in verses:
        speaker = verse['speaker']
        speaker_counts[speaker] = speaker_counts.get(speaker, 0) + 1
    
    print("\nSpeaker Statistics:")
    for speaker, count in sorted(speaker_counts.items(), key=lambda x: x[1], reverse=True):
        print(f"  {speaker}: {count} verses")

if __name__ == '__main__':
    input_file = '/Users/anurag.thakur/AndroidStudioProjects/gita/verse.json'
    output_file = '/Users/anurag.thakur/AndroidStudioProjects/gita/verse_updated.json'
    
    add_speaker_field(input_file, output_file)
    print(f"\n✓ Updated file saved to: {output_file}")
    print("  Review the output, then replace the original file if correct.")
