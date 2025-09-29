import React from 'react';
import ReactMarkdown from 'react-markdown';

export default function MarkdownViewer() {
  const markdownText = `
# Markdown Example

This is a **bold** text and this is *italic* text.

- Item 1
- Item 2
- Item 3

Artificial Intelligence (AI) is no longer just a concept from science fiction — it’s here, transforming the way we live, work, and interact.

## Table of Contents
- [Introduction](#introduction)
- [The Rise of AI](#the-rise-of-ai)
- [Current Applications](#current-applications)
- [Challenges and Ethics](#challenges-and-ethics)
- [The Road Ahead](#the-road-ahead)
- [Conclusion](#conclusion)

---

## Introduction

In the last decade, AI technologies have advanced at an **unprecedented pace**. From simple task automation to complex decision-making systems, AI is rapidly becoming embedded into our daily lives.

> "The future is already here — it's just not evenly distributed." — *William Gibson*

## The Rise of AI

The journey began with simple rule-based systems and evolved into machine learning models capable of learning from massive amounts of data. Today, we have models like GPT-4, autonomous vehicles, and even AI doctors assisting in medical diagnosis.

**Major milestones:**
- 2012: Breakthroughs in deep learning (ImageNet competition)
- 2016: AlphaGo defeats world champion in Go
- 2023: Generative AI transforms creative industries

## Current Applications

AI is touching every sector:
- **Healthcare**: Early disease detection
- **Finance**: Fraud detection and algorithmic trading
- **Entertainment**: Personalized recommendations
- **Transportation**: Self-driving cars

\`\`\`javascript
// Example of a simple AI model training loop
for (let epoch = 0; epoch < 10; epoch++) {
  trainModel();
  evaluateModel();
}
\`\`\`

## Challenges and Ethics

With great power comes great responsibility. Key challenges include:
- **Bias in AI models**
- **Data privacy**
- **Job displacement**
- **Autonomous weaponry concerns**

**Ethical AI development** is crucial to ensure technology benefits all of humanity.

## The Road Ahead

What can we expect?
- Better human-AI collaboration tools
- Advancements in general AI capabilities
- More regulations around AI use
- AI systems aligned with human values

## Conclusion

The future of AI is **bright but requires careful navigation**. It's an exciting time to be alive — and to be building the future.

Thanks for reading! 👋

---

*Written by Alice Johnson | July 1st, 2024*
`;

    return (
      <div className="max-w-4xl mx-auto px-6 py-10 bg-white dark:bg-gray-950/50 text-gray-900 dark:text-white">
        <ReactMarkdown
          components={{
            h1: ({ node, ...props }) => (
              <h1 className="text-4xl font-bold text-black dark:text-white mb-6" {...props} />
            ),
            h2: ({ node, ...props }) => (
              <h2 className="text-3xl font-semibold text-black dark:text-white mt-6 mb-4" {...props} />
            ),
            h3: ({ node, ...props }) => (
              <h3 className="text-2xl font-semibold text-black dark:text-white mt-4 mb-3" {...props} />
            ),
            p: ({ node, ...props }) => (
              <p className="text-lg leading-relaxed text-gray-700 dark:text-gray-300 mb-6" {...props} />
            ),
            li: ({ node, ...props }) => (
              <li className="ml-6 list-disc text-gray-700 dark:text-gray-300 mb-2" {...props} />
            ),
            blockquote: ({ node, ...props }) => (
              <blockquote className="border-l-4 border-gray-500 pl-4 italic text-gray-600 dark:text-gray-400 my-6" {...props} />
            ),
          }}
        >
          {markdownText}
        </ReactMarkdown>
      </div>
    );
  }
